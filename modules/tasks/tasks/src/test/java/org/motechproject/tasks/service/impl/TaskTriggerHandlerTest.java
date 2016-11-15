package org.motechproject.tasks.service.impl;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.motechproject.commons.api.DataProvider;
import org.motechproject.config.SettingsFacade;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListenerEventProxy;
import org.motechproject.tasks.constants.EventDataKeys;
import org.motechproject.tasks.constants.TaskFailureCause;
import org.motechproject.tasks.domain.enums.LogicalOperator;
import org.motechproject.tasks.domain.mds.channel.ActionEvent;
import org.motechproject.tasks.domain.mds.channel.ActionParameter;
import org.motechproject.tasks.domain.mds.channel.EventParameter;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;
import org.motechproject.tasks.domain.mds.channel.builder.ActionEventBuilder;
import org.motechproject.tasks.domain.mds.channel.builder.ActionParameterBuilder;
import org.motechproject.tasks.domain.mds.task.DataSource;
import org.motechproject.tasks.domain.mds.task.Filter;
import org.motechproject.tasks.domain.mds.task.FilterSet;
import org.motechproject.tasks.domain.mds.task.Lookup;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.domain.mds.task.TaskConfig;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.exception.ActionNotFoundException;
import org.motechproject.tasks.exception.TaskHandlerException;
import org.motechproject.tasks.service.SampleTasksEventParser;
import org.motechproject.tasks.service.TaskActivityService;
import org.motechproject.tasks.service.TaskService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.domain.enums.ParameterType.BOOLEAN;
import static org.motechproject.tasks.domain.enums.ParameterType.DATE;
import static org.motechproject.tasks.domain.enums.ParameterType.DOUBLE;
import static org.motechproject.tasks.domain.enums.ParameterType.INTEGER;
import static org.motechproject.tasks.domain.enums.ParameterType.LIST;
import static org.motechproject.tasks.domain.enums.ParameterType.LONG;
import static org.motechproject.tasks.domain.enums.ParameterType.MAP;
import static org.motechproject.tasks.domain.enums.ParameterType.TEXTAREA;
import static org.motechproject.tasks.domain.enums.ParameterType.TIME;
import static org.motechproject.tasks.domain.enums.ParameterType.UNICODE;
import static org.motechproject.tasks.domain.mds.task.OperatorType.CONTAINS;
import static org.motechproject.tasks.domain.mds.task.OperatorType.ENDSWITH;
import static org.motechproject.tasks.domain.mds.task.OperatorType.EQUALS;
import static org.motechproject.tasks.domain.mds.task.OperatorType.EQUALS_IGNORE_CASE;
import static org.motechproject.tasks.domain.mds.task.OperatorType.EQ_NUMBER;
import static org.motechproject.tasks.domain.mds.task.OperatorType.EXIST;
import static org.motechproject.tasks.domain.mds.task.OperatorType.GT;
import static org.motechproject.tasks.domain.mds.task.OperatorType.LT;
import static org.motechproject.tasks.domain.mds.task.OperatorType.STARTSWITH;
import static org.springframework.aop.support.AopUtils.getTargetClass;
import static org.springframework.util.ReflectionUtils.findMethod;

public class TaskTriggerHandlerTest extends TasksTestBase {

    public class TestObjectField {
        private int id = 6789;

        public int getId() {
            return id;
        }
    }

    public class TestObject {
        private TestObjectField field = new TestObjectField();

        public TestObjectField getField() {
            return field;
        }
    }

    public class TestService {
        public void throwException(Integer phone, String message) throws IllegalAccessException {
            throw new IllegalAccessException();
        }

        public void execute(Integer phone, String message) {

        }
    }

    @Mock
    private TaskService taskService;

    @Mock
    private TaskActivityService taskActivityService;

    @Mock
    private EventListenerRegistryService registryService;

    @Mock
    private EventRelay eventRelay;

    @Mock
    private SettingsFacade settingsFacade;

    @Mock
    private DataProvider dataProvider;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceReference serviceReference;

    @Mock
    private TasksPostExecutionHandler postExecutionHandler;

    @Mock
    private TaskRetryHandler retryHandler;

    @Mock
    private Exception exception;

    @Spy
    @InjectMocks
    private TaskActionExecutor taskActionExecutor = new TaskActionExecutor();

    @Captor
    private ArgumentCaptor<TaskHandlerException> exceptionCaptor;

    @InjectMocks
    private TaskTriggerHandler handler = new TaskTriggerHandler();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        initTask();

        when(taskService.getAllTasks()).thenReturn(tasks);
        when(settingsFacade.getProperty("task.possible.errors")).thenReturn("5");
        when(dataProvider.getName()).thenReturn(TASK_DATA_PROVIDER_NAME);
        when(taskActivityService.addTaskStarted(any(Task.class), anyMap())).thenReturn(TASK_ACTIVITY_ID);

        // do the initialization, normally called by Spring as @PostConstruct
        handler.init();
        handler.addDataProvider(dataProvider);
        handler.setBundleContext(null);

        verify(taskService).getAllTasks();
        verify(registryService).registerListener(any(EventListener.class), eq(task.getTrigger().getSubject()));
    }

    @Test
    public void shouldNotRegisterHandler() {
        EventListenerRegistryService eventListenerRegistryService = mock(EventListenerRegistryService.class);

        when(taskService.getAllTasks()).thenReturn(new ArrayList<>());

        handler.init();
        verify(eventListenerRegistryService, never()).registerListener(any(EventListener.class), anyString());
    }

    @Test
    public void shouldRegisterHandlerForSubject() {
        String subject = "org.motechproject.messagecampaign.campaign-completed";

        handler.registerHandlerFor(subject);
        ArgumentCaptor<EventListener> captor = ArgumentCaptor.forClass(EventListener.class);

        verify(registryService).registerListener(captor.capture(), eq(subject));

        MotechListenerEventProxy proxy = (MotechListenerEventProxy) captor.getValue();

        assertEquals("taskTriggerHandler", proxy.getIdentifier());
        assertEquals(handler, proxy.getBean());
        assertEquals(findMethod(getTargetClass(handler), "handle", MotechEvent.class), proxy.getMethod());
    }

    @Test
    public void shouldRegisterRetryHandlerForSubject() {
        String subject = "org.motechproject.messagecampaign.campaign-completed";

        handler.registerHandlerFor(subject, true);
        ArgumentCaptor<EventListener> captor = ArgumentCaptor.forClass(EventListener.class);

        verify(registryService).registerListener(captor.capture(), eq(subject));

        MotechListenerEventProxy proxy = (MotechListenerEventProxy) captor.getValue();

        assertEquals("taskTriggerHandler", proxy.getIdentifier());
        assertEquals(handler, proxy.getBean());
        assertEquals(findMethod(getTargetClass(handler), "handleRetry", MotechEvent.class), proxy.getMethod());
    }

    @Test
    public void shouldRegisterHandlerOneTimeForSameSubjects() {
        String subject = "org.motechproject.messagecampaign.campaign-completed";
        Method method = findMethod(getTargetClass(handler), "handle", MotechEvent.class);

        Set<EventListener> listeners = new HashSet<>();
        listeners.add(new MotechListenerEventProxy("taskTriggerHandler", this, method));

        when(registryService.getListeners(subject)).thenReturn(listeners);

        handler.registerHandlerFor(subject);
        handler.registerHandlerFor(subject);
        handler.registerHandlerFor(subject);
        handler.registerHandlerFor(subject);
        handler.registerHandlerFor(subject);
        handler.registerHandlerFor(subject);
        handler.registerHandlerFor(subject);

        verify(registryService, never()).registerListener(any(EventListener.class), eq(subject));
    }

    @Test
    public void shouldHandleErrorWhenActionIsNotFound() throws Exception {
        setTriggerEvent();

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenThrow(new ActionNotFoundException(""));

        handler.handle(createEvent());

        verifyErrorHandling("task.error.actionNotFound");
    }

    @Test
    public void shouldHandleErrorWhenActionEventParameterDoesNotContainValue() throws Exception {
        setTriggerEvent();
        setActionEvent();

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);

        task.getActions().get(0).getValues().remove("phone");

        handler.handle(createEvent());

        verifyErrorHandling("task.error.taskActionNotContainsField");
    }

    @Test
    public void shouldHandleErrorWhenActionEventParameterTemplateIsNull() throws Exception {
        setTriggerEvent();
        setActionEvent();

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);
        task.getActions().get(0).getValues().put("phone", null);

        handler.handle(createEvent());

        verifyErrorHandling("task.error.templateNull");
    }

    @Test
    public void shouldHandleErrorWhenEventParameterCanNotBeConvertedToInteger() throws Exception {
        setTriggerEvent();
        setActionEvent();

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);

        task.getActions().get(0).getValues().put("phone", "1234   d");

        handler.handle(createEvent());

        verifyErrorHandling("task.error.convertToInteger");
    }

    @Test
    public void shouldHandleErrorWhenActionEventParameterCanNotBeConvertedToLong() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setLongField();

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);

        task.getActions().get(0).getValues().put("long", "1234   d");

        handler.handle(createEvent());

        verifyErrorHandling("task.error.convertToLong");
    }

    @Test
    public void shouldHandleErrorWhenActionEventParameterCanNotBeConvertedToDouble() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setDoubleField();

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);

        task.getActions().get(0).getValues().put("double", "1234   d");

        handler.handle(createEvent());

        verifyErrorHandling("task.error.convertToDouble");
    }

    @Test
    public void shouldHandleErrorWhenActionEventParameterCanNotBeConvertedToBoolean() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setBooleanField();

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);

        task.getActions().get(0).getValues().put("boolean", "abc");

        handler.handle(createEvent());

        verifyErrorHandling("task.error.convertToBoolean");
    }

    @Test
    public void shouldHandleErrorWhenActionEventParameterCanNotBeConvertedToTime() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setTimeField();

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);

        task.getActions().get(0).getValues().put("time", "234543fgf");

        handler.handle(createEvent());

        verifyErrorHandling("task.error.convertToTime");
    }

    @Test
    public void shouldHandleErrorWhenActionEventParameterCanNotBeConvertedToDate() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setDateField();

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);

        task.getActions().get(0).getValues().put("date", "234543fgf");

        handler.handle(createEvent());

        verifyErrorHandling("task.error.convertToDate");
    }

    @Test
    public void shouldTriggerErrorWhenActionDoesNotFindDataSourceWithFailIfDataNotFoundSelected() throws Exception {
        Map<String , DataProvider> providers = new HashMap<>();
        DataProvider provider = mock(DataProvider.class);
        Map<String, String> lookup = new HashMap<>();
        lookup.put("patientId", "123");
        when(provider.lookup("Patient", "", lookup)).thenReturn(null);
        providers.put(TASK_DATA_PROVIDER_NAME, provider);
        handler.setDataProviders(providers);

        TriggerEvent trigger = new TriggerEvent();
        trigger.setSubject("trigger");
        List<EventParameter> triggerEventParameters = new ArrayList<>();
        triggerEventParameters.add(new EventParameter("patientId", "123"));
        trigger.setEventParameters(triggerEventParameters);

        ActionEvent action = new ActionEventBuilder().build();
        action.setSubject("action");
        SortedSet<ActionParameter> actionEventParameters = new TreeSet<>();
        actionEventParameters.add(new ActionParameterBuilder().setDisplayName("Patient ID").setKey("patientId")
                .setType(UNICODE).setOrder(0).build());
        action.setActionParameters(actionEventParameters);

        Task task = new Task();
        task.setName("task");
        task.setTrigger(new TaskTriggerInformation("Trigger", "channel", "module", "0.1", "trigger", "listener"));
        Map<String, String> actionValues = new HashMap<>();
        actionValues.put("patientId", "{{ad.providerId.Patient#1.patientId}}");
        task.addAction(new TaskActionInformation("Action", "channel", "module", "0.1", "action", actionValues));
        task.setId(44l);
        task.setHasRegisteredChannel(true);

        TaskConfig taskConfig = new TaskConfig();
        task.setTaskConfig(taskConfig);
        taskConfig.add(new DataSource(TASK_DATA_PROVIDER_NAME, 4L, 1L, "Patient", "provider", "specifiedName",
                asList(new Lookup("patientId", "trigger.patientId")), true));

        List<Task> tasks = asList(task);

        when(taskService.findActiveTasksForTriggerSubject("trigger")).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(action);

        setTaskActivities();
        task.setFailuresInRow(taskActivities.size());

        Map<String, Object> param = new HashMap<>(4);
        param.put("patientId", "123");

        handler.handle(new MotechEvent("trigger", param));

        verify(postExecutionHandler).handleError(anyMap(), anyMap(), eq(task), any(TaskHandlerException.class), eq(TASK_ACTIVITY_ID));
    }

    @Test
    public void shouldNotTriggerErrorWhenActionDoesNotFindDataSourceWithFailIfDataNotFoundNotSelected() throws Exception {
        Map<String, DataProvider> providers = new HashMap<>();
        DataProvider provider = mock(DataProvider.class);
        Map<String, String> lookup = new HashMap<>();
        lookup.put("patientId", "123");
        when(provider.lookup("Patient", null, lookup)).thenReturn(null);
        providers.put(TASK_DATA_PROVIDER_NAME, provider);
        handler.setDataProviders(providers);

        TriggerEvent trigger = new TriggerEvent();
        trigger.setSubject("trigger");
        List<EventParameter> triggerEventParameters = new ArrayList<>();
        triggerEventParameters.add(new EventParameter("patientId", "123"));
        trigger.setEventParameters(triggerEventParameters);

        ActionEvent action = new ActionEventBuilder().build();
        action.setSubject("action");
        SortedSet<ActionParameter> actionEventParameters = new TreeSet<>();
        actionEventParameters.add(new ActionParameterBuilder().setDisplayName("Patient ID")
                .setKey("patientId").setType(UNICODE).setOrder(0).build());
        action.setActionParameters(actionEventParameters);

        Task task = new Task();
        task.setName("task");
        task.setTrigger(new TaskTriggerInformation("Trigger", "channel", "module", "0.1", "trigger", "listener"));
        Map<String, String> actionValues = new HashMap<>();
        actionValues.put("patientId", "{{ad.12345.Patient#1.patientId}}");
        task.addAction(new TaskActionInformation("Action", "channel", "module", "0.1", "action", actionValues));
        task.setId(7l);
        task.setHasRegisteredChannel(true);

        TaskConfig taskConfig = new TaskConfig();
        task.setTaskConfig(taskConfig);
        taskConfig.add(new DataSource(TASK_DATA_PROVIDER_NAME, 3L, 1L, "Patient", "provider", "specifiedName", asList(new Lookup("patientId", "trigger.patientId")), false));

        List<Task> tasks = asList(task);

        when(taskService.findActiveTasksForTriggerSubject("trigger")).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(action);

        setTaskActivities();
        task.setFailuresInRow(taskActivities.size());

        Map<String, Object> param = new HashMap<>(4);
        param.put("patientId", "123");
        handler.handle(new MotechEvent("trigger", param));

        verify(postExecutionHandler, never()).handleError(anyMap(), anyMap(), eq(task), any(TaskHandlerException.class), eq(TASK_ACTIVITY_ID));
        verify(taskActivityService).addWarning(eq(task), eq("task.warning.notFoundObjectForType"), eq("Patient"));
    }

    @Test
    public void shouldTriggerErrorWhenFilterDoesNotFindDataSourceWithFailIfDataNotFoundSelected() throws Exception {
        Map<String, DataProvider> providers = new HashMap<>();
        DataProvider provider = mock(DataProvider.class);
        Map<String, String> lookup = new HashMap<>();
        lookup.put("patientId", "123");
        when(provider.lookup("Patient", null, lookup)).thenReturn(null);
        providers.put(TASK_DATA_PROVIDER_NAME, provider);
        handler.setDataProviders(providers);

        TriggerEvent trigger = new TriggerEvent();
        trigger.setSubject("trigger");
        List<EventParameter> triggerEventParameters = new ArrayList<>();
        triggerEventParameters.add(new EventParameter("patientId", "123"));
        trigger.setEventParameters(triggerEventParameters);

        Task task = new Task();
        task.setName("task");
        task.setId(77l);
        task.setTrigger(new TaskTriggerInformation("Trigger", "channel", "module", "0.1", "trigger", "listener"));
        task.setHasRegisteredChannel(true);
        task.setActions(Collections.<TaskActionInformation>emptyList());

        TaskConfig taskConfig = new TaskConfig();
        task.setTaskConfig(taskConfig);
        taskConfig.add(new DataSource(TASK_DATA_PROVIDER_NAME, 4L, 1L, "Patient", "provider", "specifiedName",
                asList(new Lookup("patientId", "trigger.patientId")), true));
        taskConfig.add(new FilterSet(asList(new Filter("Patient ID", "ad.12345.Patient#1.patientId", INTEGER, false, EXIST.getValue(), ""))));

        List<Task> tasks = asList(task);

        when(taskService.findActiveTasksForTriggerSubject("trigger")).thenReturn(tasks);

        setTaskActivities();
        task.setFailuresInRow(taskActivities.size());

        Map<String, Object> param = new HashMap<>(4);
        param.put("patientId", "123");
        handler.handle(new MotechEvent("trigger", param));

        verify(postExecutionHandler).handleError(anyMap(), anyMap(), eq(task), any(TaskHandlerException.class), eq(TASK_ACTIVITY_ID));
    }

    @Test
    public void shouldNotTriggerErrorWhenFilterDoesNotFindDataSourceWithFailIfDataNotFoundNotSelected() throws Exception {
        Map<String , DataProvider> providers = new HashMap<>();
        DataProvider provider = mock(DataProvider.class);
        Map<String, String> lookup = new HashMap<>();
        lookup.put("patientId", "123");
        when(provider.lookup("Patient", null, lookup)).thenReturn(null);
        providers.put(TASK_DATA_PROVIDER_NAME, provider);
        handler.setDataProviders(providers);

        TriggerEvent trigger = new TriggerEvent();
        trigger.setSubject("trigger");
        List<EventParameter> triggerEventParameters = new ArrayList<>();
        triggerEventParameters.add(new EventParameter("patientId", "123"));
        trigger.setEventParameters(triggerEventParameters);

        Task task = new Task();
        task.setName("task");
        task.setId(44l);
        task.setTrigger(new TaskTriggerInformation("Trigger", "channel", "module", "0.1", "trigger", "listener"));
        task.setHasRegisteredChannel(true);
        task.setActions(Collections.<TaskActionInformation>emptyList());

        TaskConfig taskConfig = new TaskConfig();
        task.setTaskConfig(taskConfig);
        taskConfig.add(new DataSource(TASK_DATA_PROVIDER_NAME, 4L, 1L, "Patient", "provider", "specifiedName",
                asList(new Lookup("patientId", "trigger.patientId")), false));
        taskConfig.add(new FilterSet(asList(new Filter("Patient ID", "ad.12345.Patient#1.patientId", INTEGER, false, EXIST.getValue(), ""))));

        List<Task> tasks = asList(task);

        when(taskService.findActiveTasksForTriggerSubject("trigger")).thenReturn(tasks);

        setTaskActivities();
        task.setFailuresInRow(taskActivities.size());

        Map<String, Object> param = new HashMap<>(4);
        param.put("patientId", "123");
        handler.handle(new MotechEvent("trigger", param));

        verify(postExecutionHandler, never()).handleError(anyMap(), anyMap(), eq(task), any(TaskHandlerException.class), eq(TASK_ACTIVITY_ID));
        verify(taskActivityService).addWarning(eq(task), eq("task.warning.notFoundObjectForType"), eq("Patient"));
    }

    @Test
    public void shouldSendEventAndConvertDateWithAndWithoutManipulation() throws Exception {
        setTriggerEvent();
        setActionEvent();

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);

        task.getActions().get(0).getValues().put("date1", "2012-12-21 21:21 +0100");
        actionEvent.addParameter(new ActionParameterBuilder().setDisplayName("Date1").setKey("date1")
                .setType(DATE).build(), true);
        task.getActions().get(0).getValues().put("date2", "{{trigger.startDate?datetime(yyyyy.MMMMM.dd GGG hh:mm aaa)}}");
        actionEvent.addParameter(new ActionParameterBuilder().setDisplayName("Date2").setKey("date2")
                .setType(UNICODE).build(), true);

        handler.handle(createEvent());

        verify(taskService).findActiveTasksForTriggerSubject(TRIGGER_SUBJECT);
        verify(taskService).getActionEventFor(task.getActions().get(0));

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        MotechEvent motechEvent = captorEvent.getValue();

        assertEquals(ACTION_SUBJECT, motechEvent.getSubject());

        Map<String, Object> motechEventParameters = motechEvent.getParameters();
        assertNotNull(motechEventParameters);

        assertEquals(task.getActions().get(0).getValues().get("phone"), motechEventParameters.get("phone").toString());
        assertEquals(4, motechEventParameters.size());
        assertNotNull(motechEventParameters.get("date1"));
        assertNotNull(motechEventParameters.get("date2"));
    }

    @Test
    public void shouldNotSendEventIfDataProvidersListIsNull() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setTaskActivities();
        task.setFailuresInRow(taskActivities.size());
        setAdditionalData(true);

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);

        assertTrue(task.isEnabled());

        handler.setDataProviders(null);
        handler.handle(createEvent());

        verify(taskService).findActiveTasksForTriggerSubject(TRIGGER_SUBJECT);
        verify(postExecutionHandler).handleError(eq(createEventParameters()), anyMap(), eq(task), exceptionCaptor.capture(), eq(TASK_ACTIVITY_ID));
        verify(dataProvider, never()).supports(anyString());
        verify(dataProvider, never()).lookup(anyString(), anyString(), anyMap());
        verify(postExecutionHandler, never()).handleActionExecuted(anyMap(), anyMap(), eq(TASK_ACTIVITY_ID));

        assertEquals("task.error.notFoundDataProvider", exceptionCaptor.getValue().getMessage());
    }

    @Test
    public void shouldNotSendEventIfDataProvidersListIsEmpty() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setTaskActivities();
        task.setFailuresInRow(taskActivities.size());
        setAdditionalData(true);

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);

        assertTrue(task.isEnabled());

        handler.setDataProviders(new HashMap<>());
        handler.handle(createEvent());

        verify(taskService).findActiveTasksForTriggerSubject(TRIGGER_SUBJECT);
        verify(postExecutionHandler).handleError(eq(createEventParameters()), anyMap(), eq(task), exceptionCaptor.capture(), eq(TASK_ACTIVITY_ID));

        verify(dataProvider, never()).supports(anyString());
        verify(dataProvider, never()).lookup(anyString(), anyString(), anyMap());

        assertEquals("task.error.notFoundDataProvider", exceptionCaptor.getValue().getMessage());
    }

    @Test
    public void shouldNotSendEventIfDataProviderNotFoundObject() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setTaskActivities();
        task.setFailuresInRow(taskActivities.size());
        setAdditionalData(true);

        Map<String, String> lookupFields = new HashMap<>();
        lookupFields.put("id", "123456789");

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);

        when(dataProvider.getName()).thenReturn("TEST");
        when(dataProvider.supports("TestObjectField")).thenReturn(true);
        when(dataProvider.lookup("TestObjectField", "id", lookupFields)).thenReturn(null);

        assertTrue(task.isEnabled());

        handler.handle(createEvent());

        verify(taskService).findActiveTasksForTriggerSubject(TRIGGER_SUBJECT);
        verify(dataProvider).lookup("TestObjectField", "id", lookupFields);
        verify(postExecutionHandler).handleError(eq(createEventParameters()), anyMap(), eq(task), exceptionCaptor.capture(), eq(TASK_ACTIVITY_ID));
        verify(postExecutionHandler, never()).handleActionExecuted(eq(createEventParameters()), anyMap(), eq(TASK_ACTIVITY_ID));

        assertEquals("task.error.objectOfTypeNotFound", exceptionCaptor.getValue().getMessage());
    }

    @Test
    public void shouldNotSendEventIfDataProviderObjectNotContainsField() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setTaskActivities();
        task.setFailuresInRow(taskActivities.size());
        setAdditionalData(true);

        Map<String, String> lookupFields = new HashMap<>();
        lookupFields.put("id", "123456789");

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);

        when(dataProvider.getName()).thenReturn("TEST");
        when(dataProvider.supports("TestObjectField")).thenReturn(true);
        when(dataProvider.lookup("TestObjectField", "id", lookupFields)).thenReturn(new Object());

        assertTrue(task.isEnabled());

        handler.handle(createEvent());

        verify(taskService).findActiveTasksForTriggerSubject(TRIGGER_SUBJECT);
        verify(dataProvider).lookup("TestObjectField", "id", lookupFields);
        verify(postExecutionHandler).handleError(eq(createEventParameters()), anyMap(), eq(task), exceptionCaptor.capture(), eq(TASK_ACTIVITY_ID));
        verify(postExecutionHandler, never()).handleActionExecuted(anyMap(), anyMap(), eq(TASK_ACTIVITY_ID));

        assertEquals("task.error.objectDoesNotContainField", exceptionCaptor.getValue().getMessage());
    }

    @Test
    public void shouldNotSendEventIfDateFormatInManipulationIsNotValid() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setManipulation();

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);

        task.getActions().get(0).getValues().put("manipulations", "{{trigger.startDate?dateTime(BadFormat)}}");

        handler.handle(createEvent());

        verifyErrorHandling("error.date.format");
    }

    @Test
    public void shouldGetWarningWhenManipulationHaveMistake() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setManipulation();

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);

        task.getActions().get(0).getValues().put("manipulations", "{{trigger.eventName?toUper}}");
        handler.handle(createEvent());

        verify(taskService).findActiveTasksForTriggerSubject(TRIGGER_SUBJECT);
        verify(taskService).getActionEventFor(task.getActions().get(0));

        verify(eventRelay).sendEventMessage(any(MotechEvent.class));
        verify(taskActivityService).addWarning(task, "task.warning.manipulation", "toUper");
    }

    @Test
    public void shouldPassFiltersCriteria() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setFilters();

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);

        handler.handle(createEvent());

        verify(taskService).findActiveTasksForTriggerSubject(TRIGGER_SUBJECT);
        verify(taskService).getActionEventFor(task.getActions().get(0));
        verify(eventRelay).sendEventMessage(any(MotechEvent.class));
    }

    @Test
    public void shouldNotPassFiltersCriteria() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setFilters();

        task.getTaskConfig().add(new FilterSet(asList(new Filter("ExternalID (Trigger)", "trigger.externalId", INTEGER, false, EXIST.getValue(), ""))));

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);

        handler.handle(createEvent());

        verify(taskService).findActiveTasksForTriggerSubject(TRIGGER_SUBJECT);
        verify(taskService, never()).getActionEventFor(task.getActions().get(0));
        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
        verify(taskActivityService).addTaskFiltered(TASK_ACTIVITY_ID);
    }

    @Test
    public void shouldNotPassFiltersCriteriaAndNotExecuteSecondAction() throws Exception {
        setTriggerEvent();
        setActionEvent();
        addActionFilterNotPassingCriteria();
        setSecondAction();

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(any(TaskActionInformation.class))).thenReturn(actionEvent);

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);

        handler.handle(createEvent());

        verify(taskService).findActiveTasksForTriggerSubject(TRIGGER_SUBJECT);
        verify(taskService).getActionEventFor(task.getActions().get(0));
        verify(taskService, never()).getActionEventFor(task.getActions().get(1));
        verify(eventRelay, times(1)).sendEventMessage(captor.capture());
        verify(taskActivityService, never()).addTaskFiltered(TASK_ACTIVITY_ID);

        List<MotechEvent> events = captor.getAllValues();

        assertEquals(asList(ACTION_SUBJECT), extract(events, on(MotechEvent.class).getSubject()));

        MotechEvent motechEventAction1 = events.get(0);

        assertEquals(ACTION_SUBJECT, motechEventAction1.getSubject());
        assertNotNull(motechEventAction1.getParameters());
        assertEquals(2, motechEventAction1.getParameters().size());
        assertEquals(task.getActions().get(0).getValues().get("phone"), motechEventAction1.getParameters().get("phone").toString());
        assertEquals("Hello 123456789, You have an appointment on 2012-11-20", motechEventAction1.getParameters().get("message"));
    }

    @Test
    public void shouldSendEventForGivenTrigger() throws Exception {
        setTriggerEvent();
        setActionEvent();

        setManipulation();
        setDateField();
        setTimeField();
        setFilters();
        setAdditionalData(true);
        setLongField();
        setDoubleField();
        setBooleanField();

        setListField();
        setMapField();

        setNonRequiredField();

        Map<String, String> testObjectLookup = new HashMap<>();
        testObjectLookup.put("id", "123456789-6789");

        TestObject testObject = new TestObject();
        TestObjectField testObjectField = new TestObjectField();

        Map<String, String> testObjectFieldLookup = new HashMap<>();
        testObjectFieldLookup.put("id", "123456789");

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);

        when(dataProvider.getName()).thenReturn("TEST");
        when(dataProvider.supports("TestObject")).thenReturn(true);
        when(dataProvider.lookup("TestObject", "id", testObjectLookup)).thenReturn(testObject);

        when(dataProvider.supports("TestObjectField")).thenReturn(true);
        when(dataProvider.lookup("TestObjectField", "id", testObjectFieldLookup)).thenReturn(testObjectField);

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);

        handler.handle(createEvent());

        verify(taskActivityService).addTaskStarted(task, createEventParameters());
        verify(taskService).findActiveTasksForTriggerSubject(TRIGGER_SUBJECT);
        verify(taskService).getActionEventFor(task.getActions().get(0));
        verify(eventRelay).sendEventMessage(captor.capture());

        MotechEvent motechEvent = captor.getValue();
        assertEquals(ACTION_SUBJECT, motechEvent.getSubject());

        Map<String, Object> motechEventParameters = motechEvent.getParameters();

        assertNotNull(motechEventParameters);
        assertEquals(13, motechEventParameters.size());
        assertEquals(task.getActions().get(0).getValues().get("phone"), motechEventParameters.get("phone").toString());
        assertEquals("Hello 123456789, You have an appointment on 2012-11-20", motechEventParameters.get("message"));
        assertEquals("String manipulation: Event-Name, Date manipulation: 20121120", motechEventParameters.get("manipulations"));
        assertEquals(DateTime.parse(task.getActions().get(0).getValues().get("date"), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z")), motechEventParameters.get("date"));
        assertEquals("test: 6789", motechEventParameters.get("dataSourceTrigger"));
        assertEquals("test: 6789", motechEventParameters.get("dataSourceObject"));
        assertEquals(DateTime.parse(task.getActions().get(0).getValues().get("time"), DateTimeFormat.forPattern("HH:mm Z")), motechEventParameters.get("time"));
        assertEquals(10000000000L, motechEventParameters.get("long"));
        assertEquals(true, motechEventParameters.get("boolean"));
        assertEquals(getExpectedList(), motechEventParameters.get("list"));
        assertEquals(getExpectedMap(), motechEventParameters.get("map"));
        assertNull(motechEventParameters.get("delivery_time"));
    }

    @Test
    public void shouldNotExecuteServiceMethodIfBundleContextIsNull() throws Exception {
        setTriggerEvent();
        setActionEvent();

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("throwException");
        actionEvent.setSubject(null);

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);

        handler.handle(createEvent());

        verifyErrorHandling("task.error.cantExecuteAction");
    }

    @Test
    public void shouldNotExecuteServiceMethodIfServiceReferenceIsNull() throws Exception {
        setTriggerEvent();
        setActionEvent();

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("throwException");
        actionEvent.setSubject(null);

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);
        when(bundleContext.getServiceReference(anyString())).thenReturn(null);

        handler.setBundleContext(bundleContext);
        handler.handle(createEvent());

        verify(taskActivityService).addWarning(task, "task.warning.serviceUnavailable", "TestService");
        verifyErrorHandling("task.error.cantExecuteAction");
    }

    @Test
    public void shouldThrowTaskExceptionWhenServiceMethodThrowException() throws Exception {
        TestService testService = new TestService();
        setTriggerEvent();
        setActionEvent();

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("throwException");
        actionEvent.setSubject(null);

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);
        when(bundleContext.getServiceReference("TestService")).thenReturn(serviceReference);
        when(bundleContext.getService(serviceReference)).thenReturn(testService);

        handler.setBundleContext(bundleContext);
        handler.handle(createEvent());

        verifyErrorHandling("task.error.serviceMethodInvokeError");
    }

    @Test
    public void shouldThrowTaskExceptionWhenServiceMethodNotFound() throws Exception {
        TestService testService = new TestService();
        setTriggerEvent();
        setActionEvent();

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("abc");
        actionEvent.setSubject(null);

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);
        when(bundleContext.getServiceReference("TestService")).thenReturn(serviceReference);
        when(bundleContext.getService(serviceReference)).thenReturn(testService);

        handler.setBundleContext(bundleContext);
        handler.handle(createEvent());

        verifyErrorHandling("task.error.notFoundMethodForService");
    }

    @Test
    public void shouldExecuteServiceMethod() throws Exception {
        TestService testService = new TestService();
        setTriggerEvent();
        setActionEvent();

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("execute");
        actionEvent.setSubject(null);

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);
        when(bundleContext.getServiceReference("TestService")).thenReturn(serviceReference);
        when(bundleContext.getService(serviceReference)).thenReturn(testService);

        handler.setBundleContext(bundleContext);
        handler.handle(createEvent());

        verify(taskService).findActiveTasksForTriggerSubject(TRIGGER_SUBJECT);
        verify(taskService).getActionEventFor(task.getActions().get(0));
        verify(postExecutionHandler).handleActionExecuted(anyMap(), anyMap(), eq(TASK_ACTIVITY_ID));
    }

    @Test
    public void shouldSendEventIfAdditionalDataNotFound() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setAdditionalData(false);

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("abc");

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);
        when(bundleContext.getServiceReference("TestService")).thenReturn(null);

        handler.setBundleContext(bundleContext);
        handler.handle(createEvent());

        verify(taskService).findActiveTasksForTriggerSubject(TRIGGER_SUBJECT);
        verify(taskService).getActionEventFor(task.getActions().get(0));
        verify(taskActivityService).addWarning(task, "task.warning.serviceUnavailable", actionEvent.getServiceInterface());
        verify(taskActivityService).addWarning(task, "task.warning.notFoundObjectForType", "TestObjectField");

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());

        assertEquals(ACTION_SUBJECT, captorEvent.getValue().getSubject());
    }

    @Test
    public void shouldSendEventIfServiceIsNotAvailable() throws Exception {
        setTriggerEvent();
        setActionEvent();

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("abc");

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenReturn(actionEvent);
        when(bundleContext.getServiceReference("TestService")).thenReturn(null);

        handler.setBundleContext(bundleContext);
        handler.handle(createEvent());

        verify(taskService).findActiveTasksForTriggerSubject(TRIGGER_SUBJECT);
        verify(taskService).getActionEventFor(task.getActions().get(0));
        verify(taskActivityService).addWarning(task, "task.warning.serviceUnavailable", actionEvent.getServiceInterface());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());

        assertEquals(ACTION_SUBJECT, captorEvent.getValue().getSubject());
    }

    @Test
    public void shouldHandleUnrecognizedError() throws Exception {
        setTriggerEvent();
        setActionEvent();

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("abc");

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenThrow(new RuntimeException());

        handler.setBundleContext(bundleContext);
        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> exceptionArgumentCaptor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(postExecutionHandler).handleError(eq(createEventParameters()), anyMap(), eq(task), exceptionArgumentCaptor.capture(), eq(TASK_ACTIVITY_ID));

        TaskHandlerException handlerException = exceptionArgumentCaptor.getValue();
        assertEquals("task.error.unrecognizedError", handlerException.getMessage());
        assertEquals(TaskFailureCause.TRIGGER, handlerException.getFailureCause());

        verify(postExecutionHandler, never()).handleActionExecuted(eq(createEventParameters()), anyMap(), eq(TASK_ACTIVITY_ID));
    }

    @Test
    public void shouldExecuteTwoActions() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setSecondAction();

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(any(TaskActionInformation.class))).thenReturn(actionEvent);

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);

        handler.handle(createEvent());

        verify(taskService).findActiveTasksForTriggerSubject(TRIGGER_SUBJECT);
        verify(taskService).getActionEventFor(task.getActions().get(0));
        verify(taskService).getActionEventFor(task.getActions().get(1));
        verify(eventRelay, times(2)).sendEventMessage(captor.capture());

        List<MotechEvent> events = captor.getAllValues();

        assertEquals(
                asList(ACTION_SUBJECT, ACTION_SUBJECT),
                extract(events, on(MotechEvent.class).getSubject())
        );

        MotechEvent motechEventAction1 = events.get(0);

        assertEquals(ACTION_SUBJECT, motechEventAction1.getSubject());
        assertNotNull(motechEventAction1.getParameters());
        assertEquals(2, motechEventAction1.getParameters().size());
        assertEquals(task.getActions().get(0).getValues().get("phone"), motechEventAction1.getParameters().get("phone").toString());
        assertEquals("Hello 123456789, You have an appointment on 2012-11-20", motechEventAction1.getParameters().get("message"));

        MotechEvent motechEventAction2 = events.get(1);

        assertEquals(ACTION_SUBJECT, motechEventAction2.getSubject());
        assertNotNull(motechEventAction2.getParameters());
        assertEquals(2, motechEventAction2.getParameters().size());
        assertEquals(task.getActions().get(0).getValues().get("phone"), motechEventAction2.getParameters().get("phone").toString());
        assertEquals("Hello, world! I'm second action", motechEventAction2.getParameters().get("message"));
    }

    @Test
    public void shouldHandleFormatManipulation() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setFormatManipulation();
        setAdditionalData(true);

        Map<String, String> testObjectLookup = new HashMap<>();
        testObjectLookup.put("id", "123456789-6789");

        TestObject testObject = new TestObject();
        TestObjectField testObjectField = new TestObjectField();

        Map<String, String> testObjectFieldLookup = new HashMap<>();
        testObjectFieldLookup.put("id", "123456789");

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(any(TaskActionInformation.class))).thenReturn(actionEvent);

        when(dataProvider.getName()).thenReturn("TEST");
        when(dataProvider.supports("TestObject")).thenReturn(true);
        when(dataProvider.lookup("TestObject", "id", testObjectLookup)).thenReturn(testObject);

        when(dataProvider.supports("TestObjectField")).thenReturn(true);
        when(dataProvider.lookup("TestObjectField", "id", testObjectFieldLookup)).thenReturn(testObjectField);

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);

        handler.handle(createEvent());

        verify(eventRelay).sendEventMessage(captor.capture());

        MotechEvent event = captor.getAllValues().get(0);
        assertEquals("123456789 || 6789 || YourName", event.getParameters().get("format"));
    }

    @Test
    public void shouldHandleTriggerWithCustomParser() throws Exception {
        setTriggerEvent();
        setActionEvent();

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getActionEventFor(any(TaskActionInformation.class))).thenReturn(actionEvent);
        when(taskService.findCustomParser(SampleTasksEventParser.PARSER_NAME)).thenReturn(new SampleTasksEventParser());

        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);

        handler.handle(createEvent(true));

        verify(taskActivityService).addTaskStarted(eq(task), captor.capture());
        Map<String, Object> paramsMap = captor.getValue();

        assertTrue(paramsMap.containsKey("eve"));
        assertTrue(paramsMap.containsKey("ext"));
        assertTrue(paramsMap.containsKey("fac"));
        assertTrue(paramsMap.containsKey("lis"));

        assertEquals("eve", paramsMap.get("eve"));
        assertEquals("123", paramsMap.get("ext"));
        assertEquals("987", paramsMap.get("fac"));
        assertEquals("[1,", paramsMap.get("lis"));
    }

    @Test
    public void shouldNotScheduleTaskRetriesWhenTaskIsDeleted() throws Exception {
        setTriggerEvent();
        setActionEvent();

        task.setNumberOfRetries(5);
        task.setRetryIntervalInMilliseconds(5000);

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("abc");

        when(taskService.getTask(5L)).thenReturn(null);

        MotechEvent event = createEvent();
        event.getParameters().put(EventDataKeys.TASK_ID, 5L);
        event.getParameters().put(EventDataKeys.TASK_RETRY, true);
        event.getParameters().put(EventDataKeys.JOB_SUBJECT, task.getTrigger().getEffectiveListenerRetrySubject());

        handler.setBundleContext(bundleContext);
        handler.handleRetry(event);

        verify(retryHandler, never()).handleTaskRetries(task, event.getParameters());
    }

    @Test
    public void shouldNotScheduleTaskRetriesWhenTaskIsDisabled() throws Exception {
        setTriggerEvent();
        setActionEvent();

        task.setNumberOfRetries(5);
        task.setRetryIntervalInMilliseconds(5000);
        task.setEnabled(false);

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("abc");

        when(taskService.getTask(5L)).thenReturn(task);

        MotechEvent event = createEvent();
        event.getMetadata().put(EventDataKeys.TASK_ID, 5L);
        event.getMetadata().put(EventDataKeys.JOB_SUBJECT, task.getTrigger().getEffectiveListenerRetrySubject());

        handler.setBundleContext(bundleContext);
        handler.handleRetry(event);

        verify(retryHandler, never()).handleTaskRetries(task, event.getParameters());
    }

    @Test
    public void shouldNotInvokeHandleTask() throws Exception {
        setTriggerEvent();
        setActionEvent();

        when(taskService.findActiveTasksForTriggerSubject(TRIGGER_SUBJECT)).thenReturn(tasks);
        when(taskService.getTask(9L)).thenReturn(task);

        handler.setBundleContext(bundleContext);

        Set<Long> handledTasksId = new HashSet<>();
        handledTasksId.add(9l);
        handler.setHandledTasksId(handledTasksId);

        handler.handle(createEvent());

        verify(taskActivityService, never()).addTaskStarted(task, createEventParameters());
    }

    private void verifyErrorHandling(String exceptionKey) throws ActionNotFoundException {
        verify(taskService).findActiveTasksForTriggerSubject(TRIGGER_SUBJECT);
        verify(taskService).getActionEventFor(task.getActions().get(0));
        verify(postExecutionHandler).handleError(eq(createEventParameters()), anyMap(), eq(task), exceptionCaptor.capture(), eq(TASK_ACTIVITY_ID));
        verify(postExecutionHandler, never()).handleActionExecuted(eq(createEventParameters()), anyMap(), eq(TASK_ACTIVITY_ID));

        assertEquals(exceptionKey, exceptionCaptor.getValue().getMessage());
    }

    private void setSecondAction() {
        Map<String, String> actionValues = new HashMap<>();
        actionValues.put("phone", "123456");
        actionValues.put("message", "Hello, world! I'm second action");

        task.addAction(new TaskActionInformation("sms", "SMS", "sms-bundle", "0.15", ACTION_SUBJECT, actionValues));
    }

    private void setManipulation() {
        task.getActions().get(0).getValues().put("manipulations", "String manipulation: {{trigger.eventName?toUpper?toLower?capitalize?join(-)}}, Date manipulation: {{trigger.startDate?dateTime(yyyyMMdd)}}");
        actionEvent.addParameter(new ActionParameterBuilder().setDisplayName("Manipulations").setKey("manipulations")
                .setType(TEXTAREA).build(), true);
    }

    private void setFormatManipulation() {
        task.getActions().get(0).getValues().put("format", "{{trigger.format?format({{trigger.externalId}},{{ad.12345.TestObject#2.field.id}},YourName)}}");
        actionEvent.addParameter(new ActionParameterBuilder().setDisplayName("Format").setKey("format")
                .build(), true);
    }

    private void setDateField() {
        task.getActions().get(0).getValues().put("date", "2012-12-21 21:21 +0100");
        actionEvent.addParameter(new ActionParameterBuilder().setDisplayName("Date").setKey("date")
                .setType(DATE).build(), true);
    }

    private void setTimeField() {
        task.getActions().get(0).getValues().put("time", "21:21 +0100");
        actionEvent.addParameter(new ActionParameterBuilder().setDisplayName("Time").setKey("time")
                .setType(TIME).build(), true);
    }

    private void setLongField() {
        task.getActions().get(0).getValues().put("long", "10000000000");
        actionEvent.addParameter(new ActionParameterBuilder().setDisplayName("Long").setKey("long")
                .setType(LONG).build(), true);
    }

    private void setBooleanField() {
        task.getActions().get(0).getValues().put("boolean", "true");
        actionEvent.addParameter(new ActionParameterBuilder().setDisplayName("Boolean").setKey("boolean")
                .setType(BOOLEAN).build(), true);
    }

    private void setDoubleField() {
        task.getActions().get(0).getValues().put("double", "123.5");
        actionEvent.addParameter(new ActionParameterBuilder().setDisplayName("Double").setKey("double")
                .setType(DOUBLE).build(), true);
    }

    private void setListField() {
        task.getActions().get(0).getValues().put("list", "4\n5\n{{trigger.list}}\n{{trigger.externalId}}\n{{ad.12345.TestObjectField#1.id}}");
        actionEvent.addParameter(new ActionParameterBuilder().setDisplayName("List").setKey("list")
                .setType(LIST).build(), true);
    }

    private void setMapField() {
        task.getActions().get(0).getValues().put("map", "key1:value\n{{trigger.map}}\n{{trigger.eventName}}:{{ad.12345.TestObjectField#1.id}}");
        actionEvent.addParameter(new ActionParameterBuilder().setDisplayName("Map").setKey("map")
                .setType(MAP).build(), true);
    }

    private void setAdditionalData(boolean isFail) {
        task.getActions().get(0).getValues().put("dataSourceTrigger", "test: {{ad.12345.TestObjectField#1.id}}");
        task.getActions().get(0).getValues().put("dataSourceObject", "test: {{ad.12345.TestObject#2.field.id}}");

        actionEvent.addParameter(new ActionParameterBuilder().setDisplayName("Data source by trigger")
                .setKey("dataSourceTrigger").build(), true);
        actionEvent.addParameter(new ActionParameterBuilder().setDisplayName("Data source by data source object")
                .setKey("dataSourceObject").build(), true);

        task.getTaskConfig().add(new DataSource(TASK_DATA_PROVIDER_NAME, 4L, 1L, "TestObjectField", "id", "specifiedName", asList(new Lookup("id", "{{trigger.externalId}}")), isFail));
        task.getTaskConfig().add(new DataSource(TASK_DATA_PROVIDER_NAME, 4L, 2L, "TestObject", "id", "specifiedName",asList(new Lookup("id", "{{trigger.externalId}}-{{ad.12345.TestObjectField#1.id}}")), isFail));

        handler.addDataProvider(dataProvider);
    }

    private void setFilters() {
        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter("EventName (Trigger)", "trigger.eventName", UNICODE, true, CONTAINS.getValue(), "ven"));
        filters.add(new Filter("EventName (Trigger)", "trigger.eventName", UNICODE, true, EXIST.getValue(), ""));
        filters.add(new Filter("EventName (Trigger)", "trigger.eventName", UNICODE, true, EQUALS.getValue(), "event name"));
        filters.add(new Filter("EventName (Trigger)", "trigger.eventName", UNICODE, true, EQUALS_IGNORE_CASE.getValue(), "EvEnT nAmE"));
        filters.add(new Filter("EventName (Trigger)", "trigger.eventName", UNICODE, true, STARTSWITH.getValue(), "ev"));
        filters.add(new Filter("EventName (Trigger)", "trigger.eventName", UNICODE, true, ENDSWITH.getValue(), "me"));
        filters.add(new Filter("ExternalID (Trigger)", "trigger.externalId", INTEGER, true, GT.getValue(), "19"));
        filters.add(new Filter("ExternalID (Trigger)", "trigger.externalId", INTEGER, true, LT.getValue(), "1234567891"));
        filters.add(new Filter("ExternalID (Trigger)", "trigger.externalId", INTEGER, true, EQ_NUMBER.getValue(), "123456789"));
        filters.add(new Filter("ExternalID (Trigger)", "trigger.externalId", INTEGER, true, EXIST.getValue(), ""));
        filters.add(new Filter("ExternalID (Trigger)", "trigger.externalId", INTEGER, false, GT.getValue(), "1234567891"));

        task.getTaskConfig().add(new FilterSet(filters));
    }

    private void addActionFilterNotPassingCriteria() {
        task.getTaskConfig().add(new FilterSet(asList(new Filter("ExternalID (Trigger)", "trigger.externalId", INTEGER, false, EXIST.getValue(), "")), LogicalOperator.AND, 1));
    }

    private void setNonRequiredField() {
        actionEvent.addParameter(new ActionParameterBuilder().setDisplayName("Delivery time").setKey("delivery_time")
                .setType(DATE).setRequired(false).build(), true);
    }

    private List<Object> getExpectedList() {
        List<Object> list = new ArrayList<>();
        list.addAll(asList("4", "5"));
        list.addAll(asList(1, 2, 3));
        list.add(123456789);
        list.add(6789);

        return list;
    }

    private Map<Object, Object> getExpectedMap() {
        Map<Object, Object> map = new HashMap<>();
        map.put("externalId", 123456789);
        map.put("startDate", new LocalDate(2012, 11, 20));
        map.put("key1", "value");
        map.put("event name", "6789");

        return map;
    }

}
