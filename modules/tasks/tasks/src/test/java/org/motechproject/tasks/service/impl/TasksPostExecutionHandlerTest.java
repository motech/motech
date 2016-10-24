package org.motechproject.tasks.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.config.SettingsFacade;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.tasks.constants.EventSubjects;
import org.motechproject.tasks.constants.TaskFailureCause;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActivity;
import org.motechproject.tasks.exception.TaskHandlerException;
import org.motechproject.tasks.service.TaskActivityService;
import org.motechproject.tasks.service.TaskService;

import java.util.HashMap;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.constants.EventSubjects.createHandlerFailureSubject;
import static org.motechproject.tasks.constants.TaskFailureCause.TRIGGER;

public class TasksPostExecutionHandlerTest extends TasksTestBase {

    @Mock
    private TaskService taskService;

    @Mock
    private TaskActivityService taskActivityService;

    @Mock
    private EventRelay eventRelay;

    @Mock
    private TaskRetryHandler retryHandler;

    @Mock
    private SettingsFacade settingsFacade;

    @Mock
    private TaskHandlerException taskHandlerException;

    @InjectMocks
    private TasksPostExecutionHandler postExecutionHandler = new TasksPostExecutionHandler();

    @Captor
    private ArgumentCaptor<TaskHandlerException> exceptionCaptor;

    private TaskActivity taskActivity;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        initTask();
        initTaskActivity();

        when(taskService.getAllTasks()).thenReturn(tasks);
        when(settingsFacade.getProperty("task.possible.errors")).thenReturn("5");
        when(taskActivityService.addTaskStarted(any(Task.class), anyMap())).thenReturn(TASK_ACTIVITY_ID);
        when(taskHandlerException.getFailureCause()).thenReturn(TaskFailureCause.TRIGGER);
    }

    @Test
    public void shouldResetTasksFailuresCountOnSuccess() {
        when(taskActivityService.addSuccessfulExecution(TASK_ACTIVITY_ID)).thenReturn(true);
        when(taskActivityService.getTaskActivityById(TASK_ACTIVITY_ID)).thenReturn(taskActivity);
        when(taskService.getTask(task.getId())).thenReturn(task);

        task.setFailuresInRow(4);

        postExecutionHandler.handleActionExecuted(createEventParameters(), new HashMap<>(), TASK_ACTIVITY_ID);

        assertEquals(0, task.getFailuresInRow());
        verify(taskService).save(task);
    }

    @Test
    public void shoulNotifyOnSuccessfulTask() {
        when(taskActivityService.addSuccessfulExecution(TASK_ACTIVITY_ID)).thenReturn(true);
        when(taskActivityService.getTaskActivityById(TASK_ACTIVITY_ID)).thenReturn(taskActivity);
        when(taskService.getTask(task.getId())).thenReturn(task);
        postExecutionHandler.handleActionExecuted(createEventParameters(), new HashMap<>(), TASK_ACTIVITY_ID);

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captorEvent.capture());
        verify(retryHandler).handleTaskRetries(task, createEventParameters(), true, false);

        MotechEvent motechEvent = captorEvent.getValue();
        assertEquals(EventSubjects.createHandlerSuccessSubject(task.getName()), motechEvent.getSubject());
    }

    @Test
    public void shouldDisableTaskWhenNumberOfPossibleErrorsIsExceeded() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setTaskActivities();
        task.setFailuresInRow(taskActivities.size());

        assertTrue(task.isEnabled());
        postExecutionHandler.handleError(createEventParameters(), new HashMap<>(), task, taskHandlerException, TASK_ACTIVITY_ID);

        // The task should get disabled now
        assertFalse(task.isEnabled());

        assertEquals(5, task.getFailuresInRow());

        verify(taskService).save(task);
        verify(taskActivityService).addTaskDisabledWarning(task);

        ArgumentCaptor<MotechEvent> captorEvent = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(2)).sendEventMessage(captorEvent.capture());

        List<MotechEvent> capturedEvents = captorEvent.getAllValues();
        assertEquals(asList("org.motechproject.message", createHandlerFailureSubject(task.getName(), TRIGGER)),
                extract(capturedEvents, on(MotechEvent.class).getSubject()));
    }

    private void initTaskActivity() {
        taskActivity = new TaskActivity();
        taskActivity.setId(TASK_ACTIVITY_ID);
        taskActivity.setTask(task.getId());
    }
}
