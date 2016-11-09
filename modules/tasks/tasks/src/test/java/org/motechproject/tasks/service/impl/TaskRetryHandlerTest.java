package org.motechproject.tasks.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.config.SettingsFacade;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.tasks.constants.EventDataKeys;
import org.motechproject.tasks.service.TaskService;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.constants.EventSubjects.SCHEDULE_REPEATING_JOB;

public class TaskRetryHandlerTest extends TasksTestBase {

    @Mock
    private TaskService taskService;

    @Mock
    private EventRelay eventRelay;

    @Mock
    private SettingsFacade settings;

    @InjectMocks
    private TaskRetryHandler taskRetryHandler = new TaskRetryHandler();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        initTask();
    }

    @Test
    public void shouldScheduleTaskRetriesOnFailure() throws Exception {
        setTriggerEvent();
        setActionEvent();

        task.setRetryTaskOnFailure(true);
        task.setRetryIntervalInMilliseconds(5000);

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("abc");

        when(taskService.findActiveTasksForTriggerSubject(triggerEvent.getSubject())).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenThrow(new RuntimeException());

        taskRetryHandler.handleTaskRetries(task, createEventParameters());
        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);

        verify(eventRelay).sendEventMessage(captorEvent.capture());

        MotechEvent scheduleJobEvent = captorEvent.getValue();
        assertEquals(SCHEDULE_REPEATING_JOB, scheduleJobEvent.getSubject());

        Map<String, Object> metadata = scheduleJobEvent.getMetadata();
        // We send job start time in seconds
        assertEquals(5, metadata.get(EventDataKeys.JOB_START));
        assertEquals(task.getId(), metadata.get(EventDataKeys.TASK_ID));
        assertEquals(task.getTrigger().getEffectiveListenerRetrySubject(), metadata.get(EventDataKeys.JOB_SUBJECT));
    }

    @Test
    public void shouldNotScheduleTaskRetryOnFailureWhenRetryTaskOnFailureIsFalse() throws Exception {
        setTriggerEvent();
        setActionEvent();

        task.setRetryTaskOnFailure(false);

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("abc");

        when(taskService.findActiveTasksForTriggerSubject(triggerEvent.getSubject())).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenThrow(new RuntimeException());

        MotechEvent event = createEvent();
        taskRetryHandler.handleTaskRetries(task, event.getParameters());

        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
    }
}
