package org.motechproject.tasks.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.commons.api.DataProvider;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListenerEventProxy;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskActivity;
import org.motechproject.tasks.domain.TaskAdditionalData;
import org.motechproject.tasks.domain.TaskEventInformation;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TaskHandlerException;
import org.motechproject.tasks.ex.TriggerNotFoundException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
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
import static org.motechproject.tasks.domain.OperatorType.CONTAINS;
import static org.motechproject.tasks.domain.OperatorType.ENDSWITH;
import static org.motechproject.tasks.domain.OperatorType.EQUALS;
import static org.motechproject.tasks.domain.OperatorType.EXIST;
import static org.motechproject.tasks.domain.OperatorType.GT;
import static org.motechproject.tasks.domain.OperatorType.LT;
import static org.motechproject.tasks.domain.OperatorType.STARTSWITH;
import static org.motechproject.tasks.domain.ParameterType.BOOLEAN;
import static org.motechproject.tasks.domain.ParameterType.DATE;
import static org.motechproject.tasks.domain.ParameterType.DOUBLE;
import static org.motechproject.tasks.domain.ParameterType.INTEGER;
import static org.motechproject.tasks.domain.ParameterType.LIST;
import static org.motechproject.tasks.domain.ParameterType.LONG;
import static org.motechproject.tasks.domain.ParameterType.MAP;
import static org.motechproject.tasks.domain.ParameterType.TEXTAREA;
import static org.motechproject.tasks.domain.ParameterType.TIME;
import static org.motechproject.tasks.domain.TaskActivityType.ERROR;
import static org.motechproject.tasks.events.constants.EventSubjects.createHandlerFailureSubject;
import static org.motechproject.tasks.events.constants.EventSubjects.createHandlerSuccessSubject;
import static org.motechproject.tasks.events.constants.TaskFailureCause.ACTION;
import static org.motechproject.tasks.events.constants.TaskFailureCause.DATA_SOURCE;
import static org.motechproject.tasks.events.constants.TaskFailureCause.TRIGGER;
import static org.springframework.aop.support.AopUtils.getTargetClass;
import static org.springframework.util.ReflectionUtils.findMethod;

public class TaskTriggerHandlerTest {
    private static final String TRIGGER_SUBJECT = "APPOINTMENT_CREATE_EVENT_SUBJECT";
    private static final String ACTION_SUBJECT = "SEND_SMS";
    private static final String TASK_DATA_PROVIDER_ID = "12345";

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
    TaskService taskService;

    @Mock
    TaskActivityService taskActivityService;

    @Mock
    EventListenerRegistryService registryService;

    @Mock
    EventRelay eventRelay;

    @Mock
    SettingsFacade settingsFacade;

    @Mock
    DataProvider dataProvider;

    @Mock
    BundleContext bundleContext;

    @Mock
    ServiceReference serviceReference;

    @Mock
    Exception exception;

    TaskTriggerHandler handler;

    List<Task> tasks = new ArrayList<>(1);
    List<TaskActivity> taskActivities;

    Task task;
    TriggerEvent triggerEvent;
    ActionEvent actionEvent;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        initTask();

        when(taskService.getAllTasks()).thenReturn(tasks);
        when(settingsFacade.getProperty("task.possible.errors")).thenReturn("5");

        handler = new TaskTriggerHandler(taskService, taskActivityService, registryService, eventRelay, settingsFacade);
        handler.addDataProvider(TASK_DATA_PROVIDER_ID, dataProvider);
        handler.setBundleContext(null);

        verify(taskService).getAllTasks();
        verify(registryService).registerListener(any(EventListener.class), eq(task.getTrigger().getSubject()));
    }

    @Test
    public void shouldNotRegisterHandler() {
        EventListenerRegistryService eventListenerRegistryService = mock(EventListenerRegistryService.class);

        new TaskTriggerHandler(null, null, eventListenerRegistryService, null, null);
        verify(eventListenerRegistryService, never()).registerListener(any(EventListener.class), anyString());

        when(taskService.getAllTasks()).thenReturn(new ArrayList<Task>());

        new TaskTriggerHandler(taskService, null, eventListenerRegistryService, null, null);
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
    public void shouldNotSendEventWhenTriggerNotFound() throws Exception {
        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenThrow(new TriggerNotFoundException(""));

        handler.handle(createEvent());

        verify(taskService).findTrigger(TRIGGER_SUBJECT);

        verify(taskService, never()).findTasksForTrigger(triggerEvent);
        verify(taskService, never()).getActionEventFor(task);
        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
        verify(taskActivityService, never()).addSuccess(task);
    }

    @Test
    public void shouldNotSendEventWhenActionNotFound() throws Exception {
        setTriggerEvent();

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenThrow(new ActionNotFoundException(""));

        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());
        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals(createHandlerFailureSubject(task.getName(), TRIGGER), captorEvent.getValue().getSubject());
        assertEquals("error.actionNotFound", captor.getValue().getMessage());
    }

    @Test
    public void shouldNotSendEventWhenActionEventParameterNotContainValue() throws Exception {
        setTriggerEvent();
        setActionEvent();

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);

        task.getActionInputFields().remove("phone");

        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals(createHandlerFailureSubject(task.getName(), TRIGGER), captorEvent.getValue().getSubject());
        assertEquals("error.taskNotContainsField", captor.getValue().getMessage());
    }

    @Test
    public void shouldNotSendEventWhenActionEventParameterHasNotValue() throws Exception {
        setTriggerEvent();
        setActionEvent();

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);

        task.getActionInputFields().put("phone", null);

        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals(createHandlerFailureSubject(task.getName(), TRIGGER), captorEvent.getValue().getSubject());
        assertEquals("error.templateNull", captor.getValue().getMessage());
    }

    @Test
    public void shouldNotSendEventIfActionEventParameterCanNotBeConvertedToInteger() throws Exception {
        setTriggerEvent();
        setActionEvent();

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);

        task.getActionInputFields().put("phone", "1234   d");

        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals(createHandlerFailureSubject(task.getName(), TRIGGER), captorEvent.getValue().getSubject());
        assertEquals("error.convertToInteger", captor.getValue().getMessage());
    }

    @Test
    public void shouldNotSendEventIfActionEventParameterCanNotBeConvertedToLong() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setLongField();

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);

        task.getActionInputFields().put("long", "1234   d");

        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals(createHandlerFailureSubject(task.getName(), TRIGGER), captorEvent.getValue().getSubject());
        assertEquals("error.convertToLong", captor.getValue().getMessage());
    }

    @Test
    public void shouldNotSendEventIfActionEventParameterCanNotBeConvertedToDouble() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setDoubleField();

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);

        task.getActionInputFields().put("double", "1234   d");

        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals(createHandlerFailureSubject(task.getName(), TRIGGER), captorEvent.getValue().getSubject());
        assertEquals("error.convertToDouble", captor.getValue().getMessage());
    }

    @Test
    public void shouldNotSendEventIfActionEventParameterCanNotBeConvertedToBoolean() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setBooleanField();

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);

        task.getActionInputFields().put("boolean", "abc");

        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals(createHandlerFailureSubject(task.getName(), TRIGGER), captorEvent.getValue().getSubject());
        assertEquals("error.convertToBoolean", captor.getValue().getMessage());
    }

    @Test
    public void shouldNotSendEventIfActionEventParameterCanNotBeConvertedToTime() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setTimeField();

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);

        task.getActionInputFields().put("time", "234543fgf");

        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals(createHandlerFailureSubject(task.getName(), TRIGGER), captorEvent.getValue().getSubject());
        assertEquals("error.convertToTime", captor.getValue().getMessage());
    }

    @Test
    public void shouldNotSendEventIfActionEventParameterCanNotBeConvertedToDate() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setDateField();

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);

        task.getActionInputFields().put("date", "234543fgf");

        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals(createHandlerFailureSubject(task.getName(), TRIGGER), captorEvent.getValue().getSubject());
        assertEquals("error.convertToDate", captor.getValue().getMessage());
    }

    @Test
    public void shouldDisableTaskWhenNumberPossibleErrorsIsExceeded() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setTaskActivities();

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);
        when(taskActivityService.errorsFromLastRun(task)).thenReturn(taskActivities);
        task.getActionInputFields().put("message", null);

        assertTrue(task.isEnabled());

        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());
        verify(taskActivityService).errorsFromLastRun(task);
        verify(taskService).save(task);
        verify(taskActivityService).addWarning(task);

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(2)).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        List<MotechEvent> capturedEvents = captorEvent.getAllValues();

        assertEquals(asList("org.motechproject.message", createHandlerFailureSubject(task.getName(), TRIGGER)),
                extract(capturedEvents, on(MotechEvent.class).getSubject()));

        assertFalse(task.isEnabled());
        assertEquals("error.templateNull", captor.getValue().getMessage());
    }

    @Test
    public void shouldNotSendEventIfDataProvidersListIsNull() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setTaskActivities();
        setAdditionalData(true);

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);
        when(taskActivityService.errorsFromLastRun(task)).thenReturn(taskActivities);

        assertTrue(task.isEnabled());

        handler.setDataProviders(null);
        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        verify(dataProvider, never()).supports(anyString());
        verify(dataProvider, never()).lookup(anyString(), anyMap());
        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(2)).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        List<MotechEvent> capturedEvents = captorEvent.getAllValues();

        assertEquals(asList("org.motechproject.message", createHandlerFailureSubject(task.getName(), DATA_SOURCE)),
                extract(capturedEvents, on(MotechEvent.class).getSubject()));

        assertFalse(task.isEnabled());
        assertEquals("error.notFoundDataProvider", captor.getValue().getMessage());
    }

    @Test
    public void shouldNotSendEventIfDataProvidersListIsEmpty() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setTaskActivities();
        setAdditionalData(true);

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);
        when(taskActivityService.errorsFromLastRun(task)).thenReturn(taskActivities);

        assertTrue(task.isEnabled());

        handler.setDataProviders(new HashMap<String, DataProvider>());
        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        verify(dataProvider, never()).supports(anyString());
        verify(dataProvider, never()).lookup(anyString(), anyMap());
        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(2)).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        List<MotechEvent> capturedEvents = captorEvent.getAllValues();

        assertEquals(asList("org.motechproject.message", createHandlerFailureSubject(task.getName(), DATA_SOURCE)),
                extract(capturedEvents, on(MotechEvent.class).getSubject()));

        assertFalse(task.isEnabled());
        assertEquals("error.notFoundDataProvider", captor.getValue().getMessage());
    }

    @Test
    public void shouldNotSendEventIfDataProviderNotFoundObject() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setTaskActivities();
        setAdditionalData(true);

        Map<String, String> lookupFields = new HashMap<>();
        lookupFields.put("id", "123456789");

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);
        when(taskActivityService.errorsFromLastRun(task)).thenReturn(taskActivities);

        when(dataProvider.getName()).thenReturn("TEST");
        when(dataProvider.supports("TestObjectField")).thenReturn(true);
        when(dataProvider.lookup("TestObjectField", lookupFields)).thenReturn(null);

        assertTrue(task.isEnabled());

        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(dataProvider).lookup("TestObjectField", lookupFields);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(2)).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        List<MotechEvent> capturedEvents = captorEvent.getAllValues();

        assertEquals(asList("org.motechproject.message", createHandlerFailureSubject(task.getName(), DATA_SOURCE)),
                extract(capturedEvents, on(MotechEvent.class).getSubject()));

        assertFalse(task.isEnabled());
        assertEquals("error.notFoundObjectForType", captor.getValue().getMessage());
    }

    @Test
    public void shouldNotSendEventIfDataProviderObjectNotContainsField() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setTaskActivities();
        setAdditionalData(true);

        Map<String, String> lookupFields = new HashMap<>();
        lookupFields.put("id", "123456789");

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);
        when(taskActivityService.errorsFromLastRun(task)).thenReturn(taskActivities);

        when(dataProvider.getName()).thenReturn("TEST");
        when(dataProvider.supports("TestObjectField")).thenReturn(true);
        when(dataProvider.lookup("TestObjectField", lookupFields)).thenReturn(new Object());

        assertTrue(task.isEnabled());

        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(dataProvider).lookup("TestObjectField", lookupFields);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(2)).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        List<MotechEvent> capturedEvents = captorEvent.getAllValues();

        assertEquals(asList("org.motechproject.message", createHandlerFailureSubject(task.getName(), DATA_SOURCE)),
                extract(capturedEvents, on(MotechEvent.class).getSubject()));

        assertFalse(task.isEnabled());
        assertEquals("error.objectNotContainsField", captor.getValue().getMessage());
    }

    @Test
    public void shouldNotSendEventWhenTaskIsDisabled() throws Exception {
        setTriggerEvent();

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);

        task.setEnabled(false);

        handler.handle(createEvent());

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService, never()).getActionEventFor(task);
        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
        verify(taskActivityService, never()).addSuccess(task);
    }

    @Test
    public void shouldNotSendEventIfDateFormatInManipulationIsNotValid() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setManipulation();

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);

        task.getActionInputFields().put("manipulations", "{{trigger.startDate?dateTime(BadFormat)}}");

        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals(createHandlerFailureSubject(task.getName(), TRIGGER), captorEvent.getValue().getSubject());
        assertEquals("error.date.format", captor.getValue().getMessage());
    }

    @Test
    public void shouldGetWarningWhenManipulationHaveMistake() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setManipulation();

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);

        task.getActionInputFields().put("manipulations", "{{trigger.eventName?toUper}}");
        handler.handle(createEvent());

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);

        verify(eventRelay, times(2)).sendEventMessage(any(MotechEvent.class));
        verify(taskActivityService).addWarning(task, "warning.manipulation", "toUper");
    }

    @Test
    public void shouldPassFiltersCriteria() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setFilters();

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);

        handler.handle(createEvent());

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(eventRelay, times(2)).sendEventMessage(any(MotechEvent.class));
        verify(taskActivityService).addSuccess(task);
    }

    @Test
    public void shouldNotPassFiltersCriteria() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setFilters();

        task.getFilters().add(new Filter(new EventParameter("ExternalID", "externalId", INTEGER), false, EXIST.getValue(), ""));

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);

        handler.handle(createEvent());

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService, never()).getActionEventFor(task);
        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
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

        Map<String, String> testObjectLookup = new HashMap<>();
        testObjectLookup.put("id", "123456789-6789");

        TestObject testObject = new TestObject();
        TestObjectField testObjectField = new TestObjectField();

        Map<String, String> testObjectFieldLookup = new HashMap<>();
        testObjectFieldLookup.put("id", "123456789");

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);

        when(dataProvider.getName()).thenReturn("TEST");
        when(dataProvider.supports("TestObject")).thenReturn(true);
        when(dataProvider.lookup("TestObject", testObjectLookup)).thenReturn(testObject);

        when(dataProvider.supports("TestObjectField")).thenReturn(true);
        when(dataProvider.lookup("TestObjectField", testObjectFieldLookup)).thenReturn(testObjectField);

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);

        handler.handle(createEvent());

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(eventRelay, times(2)).sendEventMessage(captor.capture());
        verify(taskActivityService).addSuccess(task);

        List<MotechEvent> events = captor.getAllValues();

        assertEquals(asList(ACTION_SUBJECT, createHandlerSuccessSubject(task.getName())),
                extract(events, on(MotechEvent.class).getSubject()));

        MotechEvent motechEvent = (MotechEvent) CollectionUtils.find(events, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return object instanceof MotechEvent && ((MotechEvent) object).getSubject().equalsIgnoreCase(ACTION_SUBJECT);
            }
        });

        assertEquals(ACTION_SUBJECT, motechEvent.getSubject());

        Map<String, Object> motechEventParameters = motechEvent.getParameters();

        assertNotNull(motechEventParameters);
        assertEquals(12, motechEventParameters.size());
        assertEquals(task.getActionInputFields().get("phone"), motechEventParameters.get("phone").toString());
        assertEquals("Hello 123456789, You have an appointment on 2012-11-20", motechEventParameters.get("message"));
        assertEquals("String manipulation: Event-Name, Date manipulation: 20121120", motechEventParameters.get("manipulations"));
        assertEquals(DateTime.parse(task.getActionInputFields().get("date"), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z")), motechEventParameters.get("date"));
        assertEquals("test: 6789", motechEventParameters.get("dataSourceTrigger"));
        assertEquals("test: 6789", motechEventParameters.get("dataSourceObject"));
        assertEquals(DateTime.parse(task.getActionInputFields().get("time"), DateTimeFormat.forPattern("HH:mm Z")), motechEventParameters.get("time"));
        assertEquals(10000000000L, motechEventParameters.get("long"));
        assertEquals(true, motechEventParameters.get("boolean"));
        assertEquals(getExpectedList(), motechEventParameters.get("list"));
        assertEquals(getExpectedMap(), motechEventParameters.get("map"));
    }

    @Test
    public void shouldNotExecuteServiceMethodIfBundleContextIsNull() throws Exception {
        setTriggerEvent();
        setActionEvent();

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("throwException");
        actionEvent.setSubject(null);

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);

        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals(createHandlerFailureSubject(task.getName(), ACTION), captorEvent.getValue().getSubject());
        assertEquals("error.cantExecuteAction", captor.getValue().getMessage());
    }

    @Test
    public void shouldNotExecuteServiceMethodIfServiceReferenceIsNull() throws Exception {
        setTriggerEvent();
        setActionEvent();

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("throwException");
        actionEvent.setSubject(null);

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);
        when(bundleContext.getServiceReference(anyString())).thenReturn(null);

        handler.setBundleContext(bundleContext);
        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addWarning(task, "warning.serviceUnavailable", "TestService");
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals(createHandlerFailureSubject(task.getName(), ACTION), captorEvent.getValue().getSubject());
        assertEquals("error.cantExecuteAction", captor.getValue().getMessage());
    }

    @Test
    public void shouldThrowTaskExceptionWhenServiceMethodThrowException() throws Exception {
        TestService testService = new TestService();
        setTriggerEvent();
        setActionEvent();

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("throwException");
        actionEvent.setSubject(null);

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);
        when(bundleContext.getServiceReference("TestService")).thenReturn(serviceReference);
        when(bundleContext.getService(serviceReference)).thenReturn(testService);

        handler.setBundleContext(bundleContext);
        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals(createHandlerFailureSubject(task.getName(), ACTION), captorEvent.getValue().getSubject());
        assertEquals("error.serviceMethodInvokeError", captor.getValue().getMessage());
    }

    @Test
    public void shouldThrowTaskExceptionWhenServiceMethodNotFound() throws Exception {
        TestService testService = new TestService();
        setTriggerEvent();
        setActionEvent();

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("abc");
        actionEvent.setSubject(null);

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);
        when(bundleContext.getServiceReference("TestService")).thenReturn(serviceReference);
        when(bundleContext.getService(serviceReference)).thenReturn(testService);

        handler.setBundleContext(bundleContext);
        handler.handle(createEvent());
        ArgumentCaptor<TaskHandlerException> captor = ArgumentCaptor.forClass(TaskHandlerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals(createHandlerFailureSubject(task.getName(), ACTION), captorEvent.getValue().getSubject());
        assertEquals("error.notFoundMethodForService", captor.getValue().getMessage());
    }

    @Test
    public void shouldExecuteServiceMethod() throws Exception {
        TestService testService = new TestService();
        setTriggerEvent();
        setActionEvent();

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("execute");
        actionEvent.setSubject(null);

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);
        when(bundleContext.getServiceReference("TestService")).thenReturn(serviceReference);
        when(bundleContext.getService(serviceReference)).thenReturn(testService);

        handler.setBundleContext(bundleContext);
        handler.handle(createEvent());

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addSuccess(task);

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());

        assertEquals(createHandlerSuccessSubject(task.getName()), captorEvent.getValue().getSubject());
    }

    @Test
    public void shouldSendEventIfAdditionalDataNotFound() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setAdditionalData(false);

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("abc");

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);
        when(bundleContext.getServiceReference("TestService")).thenReturn(null);

        handler.setBundleContext(bundleContext);
        handler.handle(createEvent());

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addWarning(task, "warning.serviceUnavailable", actionEvent.getServiceInterface());
        verify(taskActivityService, times(2)).addWarning(task, "warning.notFoundObjectForType", "TestObjectField");
        verify(taskActivityService).addSuccess(task);

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(2)).sendEventMessage(captorEvent.capture());

        assertEquals(asList(ACTION_SUBJECT, createHandlerSuccessSubject(task.getName())),
                extract(captorEvent.getAllValues(), on(MotechEvent.class).getSubject()));
    }

    @Test
    public void shouldSendEventIfServiceIsNotAvailable() throws Exception {
        setTriggerEvent();
        setActionEvent();

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("abc");

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);
        when(bundleContext.getServiceReference("TestService")).thenReturn(null);

        handler.setBundleContext(bundleContext);
        handler.handle(createEvent());

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addWarning(task, "warning.serviceUnavailable", actionEvent.getServiceInterface());
        verify(taskActivityService).addSuccess(task);

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(2)).sendEventMessage(captorEvent.capture());

        assertEquals(asList(ACTION_SUBJECT, createHandlerSuccessSubject(task.getName())),
                extract(captorEvent.getAllValues(), on(MotechEvent.class).getSubject()));
    }

    @Test
    public void shoudlCaptureUnrecognizedError() throws Exception {
        setTriggerEvent();
        setActionEvent();

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("abc");

        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenThrow(new RuntimeException());

        handler.setBundleContext(bundleContext);
        handler.handle(createEvent());
        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals(createHandlerFailureSubject(task.getName(), TRIGGER), captorEvent.getValue().getSubject());
    }

    private void initTask() throws Exception {
        TaskEventInformation trigger = new TaskEventInformation("appointments", "Appointments", "appointments-bundle", "0.15", TRIGGER_SUBJECT);
        TaskActionInformation action = new TaskActionInformation("sms", "SMS", "sms-bundle", "0.15", ACTION_SUBJECT);

        Map<String, String> actionInputFields = new HashMap<>();
        actionInputFields.put("phone", "123456");
        actionInputFields.put("message", "Hello {{trigger.externalId}}, You have an appointment on {{trigger.startDate}}");

        task = new Task("name", trigger, action, actionInputFields);
        task.setId("taskId1");
        tasks.add(task);
    }

    private void setManipulation() {
        task.getActionInputFields().put("manipulations", "String manipulation: {{trigger.eventName?toUpper?toLower?capitalize?join(-)}}, Date manipulation: {{trigger.startDate?dateTime(yyyyMMdd)}}");
        actionEvent.addParameter(new ActionParameter("Manipulations", "manipulations", TEXTAREA), true);
    }

    private void setDateField() {
        task.getActionInputFields().put("date", "2012-12-21 21:21 +0100");
        actionEvent.addParameter(new ActionParameter("Date", "date", DATE), true);
    }

    private void setTimeField() {
        task.getActionInputFields().put("time", "21:21 +0100");
        actionEvent.addParameter(new ActionParameter("Time", "time", TIME), true);
    }

    private void setLongField() {
        task.getActionInputFields().put("long", "10000000000");
        actionEvent.addParameter(new ActionParameter("Long", "long", LONG), true);
    }

    private void setBooleanField() {
        task.getActionInputFields().put("boolean", "true");
        actionEvent.addParameter(new ActionParameter("Boolean", "boolean", BOOLEAN), true);
    }

    private void setDoubleField() {
        task.getActionInputFields().put("double", "123.5");
        actionEvent.addParameter(new ActionParameter("Double", "double", DOUBLE), true);
    }

    private void setListField() {
        task.getActionInputFields().put("list", "4\n5\n{{trigger.list}}\n{{trigger.externalId}}\n{{ad.12345.TestObjectField#1.id}}");
        actionEvent.addParameter(new ActionParameter("List", "list", LIST), true);
    }

    private void setMapField() {
        task.getActionInputFields().put("map", "key1:value\n{{trigger.map}}\n{{trigger.eventName}}:{{ad.12345.TestObjectField#1.id}}");
        actionEvent.addParameter(new ActionParameter("Map", "map", MAP), true);
    }

    private void setAdditionalData(boolean isFail) {
        task.getActionInputFields().put("dataSourceTrigger", "test: {{ad.12345.TestObjectField#1.id}}");
        task.getActionInputFields().put("dataSourceObject", "test: {{ad.12345.TestObject#2.field.id}}");

        actionEvent.addParameter(new ActionParameter("Data source by trigger", "dataSourceTrigger"), true);
        actionEvent.addParameter(new ActionParameter("Data source by data source object", "dataSourceObject"), true);

        Map<String, List<TaskAdditionalData>> additionalData = new HashMap<>(2);
        additionalData.put("12345", asList(
                new TaskAdditionalData(1L, "TestObjectField", "id", "{{trigger.externalId}}", isFail),
                new TaskAdditionalData(2L, "TestObject", "id", "{{trigger.externalId}}-{{ad.12345.TestObjectField#1.id}}", isFail)
        ));

        task.setAdditionalData(additionalData);

        handler.addDataProvider(TASK_DATA_PROVIDER_ID, dataProvider);
    }

    private void setTriggerEvent() {
        List<EventParameter> triggerEventParameters = new ArrayList<>();
        triggerEventParameters.add(new EventParameter("ExternalID", "externalId"));
        triggerEventParameters.add(new EventParameter("StartDate", "startDate", DATE));
        triggerEventParameters.add(new EventParameter("EndDate", "endDate", DATE));
        triggerEventParameters.add(new EventParameter("FacilityId", "facilityId"));
        triggerEventParameters.add(new EventParameter("EventName", "eventName"));
        triggerEventParameters.add(new EventParameter("List", "list", LIST));
        triggerEventParameters.add(new EventParameter("Map", "map", MAP));

        triggerEvent = new TriggerEvent();
        triggerEvent.setSubject(TRIGGER_SUBJECT);
        triggerEvent.setEventParameters(triggerEventParameters);
    }

    private void setActionEvent() {
        SortedSet<ActionParameter> actionEventParameters = new TreeSet<>();
        actionEventParameters.add(new ActionParameter("Phone", "phone", INTEGER, 0));
        actionEventParameters.add(new ActionParameter("Message", "message", TEXTAREA, 1));

        actionEvent = new ActionEvent();
        actionEvent.setSubject(ACTION_SUBJECT);
        actionEvent.setActionParameters(actionEventParameters);
    }

    private void setTaskActivities() {
        taskActivities = new ArrayList<>(5);
        taskActivities.add(new TaskActivity("Error1", task.getId(), ERROR));
        taskActivities.add(new TaskActivity("Error2", task.getId(), ERROR));
        taskActivities.add(new TaskActivity("Error3", task.getId(), ERROR));
        taskActivities.add(new TaskActivity("Error4", task.getId(), ERROR));
        taskActivities.add(new TaskActivity("Error5", task.getId(), ERROR));
    }

    private void setFilters() {
        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter(new EventParameter("EventName", "eventName"), true, CONTAINS.getValue(), "ven"));
        filters.add(new Filter(new EventParameter("EventName", "eventName"), true, EXIST.getValue(), ""));
        filters.add(new Filter(new EventParameter("EventName", "eventName"), true, EQUALS.getValue(), "event name"));
        filters.add(new Filter(new EventParameter("EventName", "eventName"), true, STARTSWITH.getValue(), "ev"));
        filters.add(new Filter(new EventParameter("EventName", "eventName"), true, ENDSWITH.getValue(), "me"));
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", INTEGER), true, GT.getValue(), "19"));
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", INTEGER), true, LT.getValue(), "1234567891"));
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", INTEGER), true, EQUALS.getValue(), "123456789"));
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", INTEGER), true, EXIST.getValue(), ""));
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", INTEGER), false, GT.getValue(), "1234567891"));

        task.setFilters(filters);
    }

    private MotechEvent createEvent() {
        Map<String, Object> param = new HashMap<>(4);
        param.put("externalId", 123456789);
        param.put("startDate", new LocalDate(2012, 11, 20));
        param.put("map", new HashMap<>(param));
        param.put("endDate", new LocalDate(2012, 11, 29));
        param.put("facilityId", 987654321);
        param.put("eventName", "event name");
        param.put("list", Arrays.asList(1, 2, 3));

        return new MotechEvent(TRIGGER_SUBJECT, param);
    }

    private List<Object> getExpectedList() {
        List<Object> list = new ArrayList<>();
        list.addAll(Arrays.asList("4", "5"));
        list.addAll(Arrays.asList(1, 2, 3));
        list.add(123456789);
        list.add(6789);

        return list;
    }

    private Map<Object, Object> getExpectedMap() {
        Map<Object, Object> map = new HashMap<>();
        map.put("externalId", 123456789);
        map.put("startDate", new LocalDate(2012, 11, 20));
        map.put("key1", "value");
        map.put("event name", 6789);

        return map;
    }

}
