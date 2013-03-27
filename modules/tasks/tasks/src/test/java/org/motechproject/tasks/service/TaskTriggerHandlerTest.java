package org.motechproject.tasks.service;

import org.apache.commons.lang.exception.ExceptionUtils;
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
import org.motechproject.tasks.EventKeys;
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
import org.motechproject.tasks.ex.TaskException;
import org.motechproject.tasks.ex.TaskTriggerException;
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
import static org.motechproject.tasks.domain.ParameterType.DATE;
import static org.motechproject.tasks.domain.ParameterType.DOUBLE;
import static org.motechproject.tasks.domain.ParameterType.INTEGER;
import static org.motechproject.tasks.domain.ParameterType.TEXTAREA;
import static org.motechproject.tasks.domain.TaskActivityType.ERROR;
import static org.springframework.aop.support.AopUtils.getTargetClass;
import static org.springframework.util.ReflectionUtils.findMethod;

public class TaskTriggerHandlerTest {
    private static final String TRIGGER_SUBJECT = "APPOINTMENT_CREATE_EVENT_SUBJECT";
    private static final String ACTION_SUBJECT = "SEND_SMS";
    private static final String TASK_DATA_PROVIDER_ID = "12345";

    private class TestObjectField {
        private int id = 6789;

        public int getId() {
            return id;
        }
    }

    private class TestObject {
        private TestObjectField field = new TestObjectField();

        public TestObjectField getField() {
            return field;
        }
    }

    private class TestService {
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
        ArgumentCaptor<TaskTriggerException> captor = ArgumentCaptor.forClass(TaskTriggerException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());
        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals("TRIGGER_FAILED", captorEvent.getValue().getSubject());
        assertEquals("error.actionNotFound", captor.getValue().getMessageKey());
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
        ArgumentCaptor<TaskException> captor = ArgumentCaptor.forClass(TaskException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals("TRIGGER_FAILED", captorEvent.getValue().getSubject());
        assertEquals("error.taskNotContainsField", captor.getValue().getMessageKey());
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
        ArgumentCaptor<TaskException> captor = ArgumentCaptor.forClass(TaskException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals("TRIGGER_FAILED", captorEvent.getValue().getSubject());
        assertEquals("error.templateNull", captor.getValue().getMessageKey());
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
        ArgumentCaptor<TaskException> captor = ArgumentCaptor.forClass(TaskException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals("TRIGGER_FAILED", captorEvent.getValue().getSubject());
        assertEquals("error.convertToInteger", captor.getValue().getMessageKey());
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
        ArgumentCaptor<TaskException> captor = ArgumentCaptor.forClass(TaskException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals("TRIGGER_FAILED", captorEvent.getValue().getSubject());
        assertEquals("error.convertToDouble", captor.getValue().getMessageKey());
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
        ArgumentCaptor<TaskException> captor = ArgumentCaptor.forClass(TaskException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals("TRIGGER_FAILED", captorEvent.getValue().getSubject());
        assertEquals("error.convertToDate", captor.getValue().getMessageKey());
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
        ArgumentCaptor<TaskException> captor = ArgumentCaptor.forClass(TaskException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());
        verify(taskActivityService, times(2)).errorsFromLastRun(task);
        verify(taskService).save(task);
        verify(taskActivityService).addWarning(task);

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals("TRIGGER_FAILED", captorEvent.getValue().getSubject());
        assertFalse(task.isEnabled());
        assertEquals("error.templateNull", captor.getValue().getMessageKey());
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
        ArgumentCaptor<TaskException> captor = ArgumentCaptor.forClass(TaskException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        verify(dataProvider, never()).supports(anyString());
        verify(dataProvider, never()).lookup(anyString(), anyMap());
        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals("TRIGGER_FAILED", captorEvent.getValue().getSubject());
        assertFalse(task.isEnabled());
        assertEquals("error.notFoundDataProvider", captor.getValue().getMessageKey());
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
        ArgumentCaptor<TaskException> captor = ArgumentCaptor.forClass(TaskException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        verify(dataProvider, never()).supports(anyString());
        verify(dataProvider, never()).lookup(anyString(), anyMap());
        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals("TRIGGER_FAILED", captorEvent.getValue().getSubject());
        assertFalse(task.isEnabled());
        assertEquals("error.notFoundDataProvider", captor.getValue().getMessageKey());
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
        ArgumentCaptor<TaskException> captor = ArgumentCaptor.forClass(TaskException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(dataProvider).lookup("TestObjectField", lookupFields);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals("TRIGGER_FAILED", captorEvent.getValue().getSubject());
        assertFalse(task.isEnabled());
        assertEquals("error.notFoundObjectForType", captor.getValue().getMessageKey());
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
        ArgumentCaptor<TaskException> captor = ArgumentCaptor.forClass(TaskException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(dataProvider).lookup("TestObjectField", lookupFields);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals("TRIGGER_FAILED", captorEvent.getValue().getSubject());
        assertFalse(task.isEnabled());
        assertEquals("error.objectNotContainsField", captor.getValue().getMessageKey());
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
        ArgumentCaptor<TaskException> captor = ArgumentCaptor.forClass(TaskException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals("TRIGGER_FAILED", captorEvent.getValue().getSubject());
        assertEquals("error.date.format", captor.getValue().getMessageKey());
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
        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);
        handler.handle(createEvent());

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);

        verify(eventRelay).sendEventMessage(captor.capture());
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

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);

        handler.handle(createEvent());

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(eventRelay).sendEventMessage(captor.capture());
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
        setFilters();
        setAdditionalData(true);
        setDoubleField();

        Map<String, String> testObjectLookup = new HashMap<>();
        testObjectLookup.put("id", "6789");

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
        verify(eventRelay).sendEventMessage(captor.capture());
        verify(taskActivityService).addSuccess(task);

        MotechEvent motechEvent = captor.getValue();

        assertNotNull(motechEvent);
        assertNotNull(motechEvent.getSubject());
        assertNotNull(motechEvent.getParameters());

        assertEquals(7, motechEvent.getParameters().size());
        assertEquals(ACTION_SUBJECT, motechEvent.getSubject());
        assertEquals(task.getActionInputFields().get("phone"), motechEvent.getParameters().get("phone").toString());
        assertEquals("Hello 123456789, You have an appointment on 2012-11-20", motechEvent.getParameters().get("message"));
        assertEquals("String manipulation: Event-Name, Date manipulation: 20121120", motechEvent.getParameters().get("manipulations"));
        assertEquals(DateTime.parse(task.getActionInputFields().get("date"), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z")), motechEvent.getParameters().get("date"));
        assertEquals("test: 6789", motechEvent.getParameters().get("dataSourceTrigger"));
        assertEquals("test: 6789", motechEvent.getParameters().get("dataSourceObject"));
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
        ArgumentCaptor<TaskException> captor = ArgumentCaptor.forClass(TaskException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals("ACTION_FAILED", captorEvent.getValue().getSubject());
        assertEquals("error.cantExecuteAction", captor.getValue().getMessageKey());
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
        ArgumentCaptor<TaskException> captor = ArgumentCaptor.forClass(TaskException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addWarning(task, "warning.serviceUnavailable", "TestService");
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals("ACTION_FAILED", captorEvent.getValue().getSubject());
        assertEquals("error.cantExecuteAction", captor.getValue().getMessageKey());
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
        ArgumentCaptor<TaskException> captor = ArgumentCaptor.forClass(TaskException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals("ACTION_FAILED", captorEvent.getValue().getSubject());
        assertEquals("error.serviceMethodInvokeError", captor.getValue().getMessageKey());
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
        ArgumentCaptor<TaskException> captor = ArgumentCaptor.forClass(TaskException.class);

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskActivityService).addError(eq(task), captor.capture());

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(taskActivityService, never()).addSuccess(task);

        assertEquals("ACTION_FAILED", captorEvent.getValue().getSubject());
        assertEquals("error.notFoundMethodForService", captor.getValue().getMessageKey());
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

        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
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
        verify(eventRelay).sendEventMessage(any(MotechEvent.class));
        verify(taskActivityService).addSuccess(task);
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
        verify(eventRelay).sendEventMessage(any(MotechEvent.class));
        verify(taskActivityService).addSuccess(task);
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

        assertEquals("TRIGGER_FAILED", captorEvent.getValue().getSubject());
    }

    private void initTask() throws Exception {
        TaskEventInformation trigger = new TaskEventInformation("Appointments", "appointments-bundle", "0.15", TRIGGER_SUBJECT);
        TaskActionInformation action = new TaskActionInformation("SMS", "sms-bundle", "0.15", ACTION_SUBJECT);

        Map<String, String> actionInputFields = new HashMap<>();
        actionInputFields.put("phone", "123456");
        actionInputFields.put("message", "Hello {{trigger.externalId}}, You have an appointment on {{trigger.startDate}}");

        task = new Task(trigger, action, actionInputFields, "name");
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

    private void setDoubleField() {
        task.getActionInputFields().put("double", "123.5");
        actionEvent.addParameter(new ActionParameter("Double", "double", DOUBLE), true);
    }

    private void setAdditionalData(boolean isFail) {
        task.getActionInputFields().put("dataSourceTrigger", "test: {{ad.12345.TestObjectField#1.id}}");
        task.getActionInputFields().put("dataSourceObject", "test: {{ad.12345.TestObject#2.field.id}}");

        actionEvent.addParameter(new ActionParameter("Data source by trigger", "dataSourceTrigger"), true);
        actionEvent.addParameter(new ActionParameter("Data source by data source object", "dataSourceObject"), true);

        Map<String, List<TaskAdditionalData>> additionalData = new HashMap<>(2);
        additionalData.put("12345", Arrays.asList(
                new TaskAdditionalData(1L, "TestObjectField", "id", "trigger.externalId", isFail),
                new TaskAdditionalData(2L, "TestObject", "id", "ad.12345.TestObjectField#1.id", isFail)
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
        param.put("endDate", new LocalDate(2012, 11, 29));
        param.put("facilityId", 987654321);
        param.put("eventName", "event name");

        return new MotechEvent(TRIGGER_SUBJECT, param);
    }

    private Map<String, Object> createEventParameters(Task task, TaskException e) {
        Map<String, Object> param = new HashMap<>();
        param.put(EventKeys.TASK_FAIL_MESSAGE, e.getMessageKey());
        param.put(EventKeys.TASK_FAIL_STACK_TRACE, ExceptionUtils.getStackTrace(e));
        param.put(EventKeys.TASK_FAIL_FAILURE_DATE, DateTime.now());
        param.put(EventKeys.TASK_FAIL_FAILURE_NUMBER, taskActivityService.errorsFromLastRun(task));
        param.put(EventKeys.TASK_FAIL_TRIGGER_DISABLED, task.isEnabled());
        param.put(EventKeys.TASK_FAIL_TASK_ID, task.getId());
        param.put(EventKeys.TASK_FAIL_TASK_NAME, task.getName());
        return param;
    }

}
