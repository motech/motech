package org.motechproject.tasks.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.tasks.domain.EventParamType;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Level;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskEvent;
import org.motechproject.tasks.domain.TaskStatusMessage;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TriggerNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TaskTriggerHandlerTest {
    private static final String TRIGGER_SUBJECT = "APPOINTMENT_CREATE_EVENT_SUBJECT";
    private static final String ACTION_SUBJECT = "SEND_SMS";

    @Mock
    TaskService taskService;

    @Mock
    TaskStatusMessageService taskStatusMessageService;

    @Mock
    EventListenerRegistryService registryService;

    @Mock
    SettingsFacade settingsFacade;

    @Mock
    EventRelay eventRelay;

    TaskTriggerHandler handler;

    List<Task> tasks;
    List<TaskStatusMessage> messages;
    Task task;
    TaskEvent triggerEvent;
    TaskEvent actionEvent;

    @Before
    public void setup() throws Exception {
        initTest();

        when(taskService.getAllTasks()).thenReturn(tasks);
        when(settingsFacade.getProperty("task.possible.errors")).thenReturn("5");

        handler = new TaskTriggerHandler(taskService, taskStatusMessageService, registryService, eventRelay, settingsFacade);

        verify(taskService).getAllTasks();
        verify(registryService).registerListener(any(EventListener.class), anyString());
    }

    @Test
    public void test_handler_triggerNotFound() throws Exception {
        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenThrow(new TriggerNotFoundException(""));

        handler.handler(createEvent());

        verify(taskService).findTrigger(TRIGGER_SUBJECT);

        verify(taskService, never()).findTasksForTrigger(triggerEvent);
        verify(taskService, never()).getActionEventFor(task);
        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
        verify(taskStatusMessageService, never()).addSuccess(task);
    }

    @Test
    public void test_handler_actionNotFound() throws Exception {
        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenThrow(new ActionNotFoundException(""));

        handler.handler(createEvent());

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskStatusMessageService).addError(eq(task), anyString());

        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
        verify(taskStatusMessageService, never()).addSuccess(task);
    }

    @Test
    public void test_handler_actionWithoutSubject() throws Exception {
        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);

        actionEvent.setSubject(null);

        handler.handler(createEvent());

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskStatusMessageService).addError(eq(task), anyString());

        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
        verify(taskStatusMessageService, never()).addSuccess(task);
    }

    @Test
    public void test_handler_actionEventParameterWithoutValue() throws Exception {
        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);

        task.getActionInputFields().put("phone", null);

        handler.handler(createEvent());

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskStatusMessageService).addError(eq(task), anyString());

        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
        verify(taskStatusMessageService, never()).addSuccess(task);
    }

    @Test
    public void test_handler_taskDisable() throws Exception {
        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);
        when(taskStatusMessageService.errorsFromLastRun(task)).thenReturn(messages);

        task.getActionInputFields().put("message", null);

        assertTrue(task.isEnabled());

        handler.handler(createEvent());

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(taskStatusMessageService).addError(eq(task), anyString());
        verify(taskStatusMessageService).errorsFromLastRun(task);
        verify(taskService).save(task);
        verify(taskStatusMessageService).addWarning(task);

        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
        verify(taskStatusMessageService, never()).addSuccess(task);

        assertFalse(task.isEnabled());
    }

    @Test
    public void test_handler() throws Exception {
        when(taskService.findTrigger(TRIGGER_SUBJECT)).thenReturn(triggerEvent);
        when(taskService.findTasksForTrigger(triggerEvent)).thenReturn(tasks);
        when(taskService.getActionEventFor(task)).thenReturn(actionEvent);

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);

        handler.handler(createEvent());

        verify(taskService).findTrigger(TRIGGER_SUBJECT);
        verify(taskService).findTasksForTrigger(triggerEvent);
        verify(taskService).getActionEventFor(task);
        verify(eventRelay).sendEventMessage(captor.capture());
        verify(taskStatusMessageService).addSuccess(task);

        MotechEvent motechEvent = captor.getValue();

        assertNotNull(motechEvent);
        assertNotNull(motechEvent.getSubject());
        assertNotNull(motechEvent.getParameters());

        assertEquals(2, motechEvent.getParameters().size());
        assertEquals(ACTION_SUBJECT, motechEvent.getSubject());
        assertEquals(task.getActionInputFields().get("phone"), motechEvent.getParameters().get("phone"));
        assertEquals("Hello 123456789, You have an appointment on 2012-11-20", motechEvent.getParameters().get("message"));
    }

    private MotechEvent createEvent() {
        Map<String, Object> param = new HashMap<>(4);
        param.put("externalId", 123456789);
        param.put("startDate", new LocalDate(2012, 11, 20));
        param.put("endDate", new LocalDate(2012, 11, 29));
        param.put("facilityId", 987654321);

        return new MotechEvent(TRIGGER_SUBJECT, param);
    }

    private void initTest() throws Exception {
        initMocks(this);

        tasks = new ArrayList<>();

        String trigger = String.format("Appointments:appointments-bundle:0.15:%s", TRIGGER_SUBJECT);
        String action = String.format("SMS:sms-bundle:0.15:%s", ACTION_SUBJECT);

        Map<String, String> actionInputFields = new HashMap<>();
        actionInputFields.put("phone", "123456");
        actionInputFields.put("message", "Hello {{externalId}}, You have an appointment on {{startDate}}");

        task = new Task(trigger, action, actionInputFields);
        task.setId("taskId1");

        tasks.add(task);

        List<EventParameter> triggerEventParameters = new ArrayList<>();
        triggerEventParameters.add(new EventParameter("ExternalID", "externalId"));
        triggerEventParameters.add(new EventParameter("StartDate", "startDate"));
        triggerEventParameters.add(new EventParameter("EndDate", "endDate"));
        triggerEventParameters.add(new EventParameter("FacilityId", "facilityId"));

        triggerEvent = new TaskEvent();
        triggerEvent.setSubject(TRIGGER_SUBJECT);
        triggerEvent.setEventParameters(triggerEventParameters);

        List<EventParameter> actionEventParameters = new ArrayList<>();
        actionEventParameters.add(new EventParameter("Phone", "phone"));
        actionEventParameters.add(new EventParameter("Message", "message", EventParamType.TEXTAREA));

        actionEvent = new TaskEvent();
        actionEvent.setSubject(ACTION_SUBJECT);
        actionEvent.setEventParameters(actionEventParameters);
        actionEvent.setDisplayName("SMS");

        messages = new ArrayList<>();
        messages.add(new TaskStatusMessage("Error1", task.getId(), Level.ERROR));
        messages.add(new TaskStatusMessage("Error2", task.getId(), Level.ERROR));
        messages.add(new TaskStatusMessage("Error3", task.getId(), Level.ERROR));
        messages.add(new TaskStatusMessage("Error4", task.getId(), Level.ERROR));
        messages.add(new TaskStatusMessage("Error5", task.getId(), Level.ERROR));
    }
}
