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
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.constants.EventSubjects.createHandlerFailureSubject;
import static org.motechproject.tasks.constants.TaskFailureCause.TRIGGER;
import static org.motechproject.tasks.web.domain.SettingsDto.TASK_PROPERTIES_FILE_NAME;

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

        MotechEvent motechEvent = captorEvent.getValue();
        assertEquals(EventSubjects.createHandlerSuccessSubject(task.getName()), motechEvent.getSubject());
    }

    @Test
    public void shouldDisableTaskWhenNumberOfPossibleErrorsIsExceeded() throws Exception {
        setTriggerEvent();
        setActionEvent();
        setTaskActivities();
        task.setFailuresInRow(taskActivities.size());

        when(settingsFacade.getRawConfig(TASK_PROPERTIES_FILE_NAME)).thenReturn(TASK_RETRIES);

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

    @Test
    public void shouldNotScheduleTaskRetryOnFailureWhenNumberOfRetriesIsZero() throws Exception {
        setTriggerEvent();
        setActionEvent();
        Map<String, Object> params = setEventParameters();

        task.setRetryTaskOnFailure(true);

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("abc");

        when(taskService.findActiveTasksForTriggerSubject(triggerEvent.getSubject())).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenThrow(new RuntimeException());
        when(settingsFacade.getRawConfig(TASK_PROPERTIES_FILE_NAME)).thenReturn(null);

        postExecutionHandler.handleError(params, new HashMap<>(), task, taskHandlerException, TASK_ACTIVITY_ID);

        params.put(TASK_RETRY_NUMBER, 2);

        // task number of retries is 0, we should not send schedule job event
        verify(retryHandler, never()).handleTaskRetries(task, params);
    }

    @Test
    public void shouldNotScheduleTaskRetryOnFailureWhenAllRetriesWereExecuted() throws Exception {
        setTriggerEvent();
        setActionEvent();
        Map<String, Object> params = setEventParameters();

        task.setRetryTaskOnFailure(true);

        actionEvent.setServiceInterface("TestService");
        actionEvent.setServiceMethod("abc");

        when(taskService.findActiveTasksForTriggerSubject(triggerEvent.getSubject())).thenReturn(tasks);
        when(taskService.getActionEventFor(task.getActions().get(0))).thenThrow(new RuntimeException());
        when(settingsFacade.getRawConfig(TASK_PROPERTIES_FILE_NAME)).thenReturn(TASK_RETRIES);

        postExecutionHandler.handleError(params, new HashMap<>(), task, taskHandlerException, TASK_ACTIVITY_ID);

        params.put(TASK_RETRY_NUMBER, 2);

        verify(retryHandler, never()).handleTaskRetries(task, params);
    }

    private void initTaskActivity() {
        taskActivity = new TaskActivity();
        taskActivity.setId(TASK_ACTIVITY_ID);
        taskActivity.setTask(task.getId());
    }

    private Map<String, Object> setEventParameters() {
        Map<String, Object> params = createEventParameters();
        params.put(TASK_RETRY_NUMBER, 1);
        return params;
    }
}
