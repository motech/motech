package org.motechproject.tasks.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActivity;
import org.motechproject.tasks.domain.enums.TaskActivityType;
import org.motechproject.tasks.domain.mds.task.TaskExecutionProgress;
import org.motechproject.tasks.exception.TaskHandlerException;
import org.motechproject.tasks.repository.TaskActivitiesDataService;
import org.motechproject.tasks.service.TaskActivityService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.exception.ExceptionUtils.getStackTrace;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.constants.TaskFailureCause.TRIGGER;
import static org.motechproject.tasks.domain.enums.TaskActivityType.ERROR;
import static org.motechproject.tasks.domain.enums.TaskActivityType.SUCCESS;
import static org.motechproject.tasks.domain.enums.TaskActivityType.WARNING;

public class TaskActivityServiceImplTest {

    private static final Long TASK_ID = 12345l;
    private static final Long TASK_ACTIVITY_ID  = 11L;
    private static final List<String> ERROR_FIELD = asList("phone");

    private List<TaskActivity> activities;

    @Mock
    TaskActivitiesDataService taskActivitiesDataService;

    TaskActivityService activityService;

    Task task;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        activityService = new TaskActivityServiceImpl(taskActivitiesDataService);
        activities = createTaskActivities();

        task = new Task();
        task.setId(TASK_ID);
        task.setFailuresInRow(0);
    }

    @Test
    public void shouldAddErrorActivityWithTaskException() {
        when(taskActivitiesDataService.findById(TASK_ACTIVITY_ID)).thenReturn(createInProgress());
        String messageKey = "error.notFoundTrigger";
        TaskHandlerException exception = new TaskHandlerException(TRIGGER, messageKey, ERROR_FIELD.get(0));

        ArgumentCaptor<TaskActivity> captor = ArgumentCaptor.forClass(TaskActivity.class);

        activityService.addFailedExecution(TASK_ACTIVITY_ID, exception);

        verify(taskActivitiesDataService).update(captor.capture());

        assertActivity(messageKey, ERROR_FIELD, TASK_ID, TaskActivityType.ERROR, getStackTrace(exception), null, captor.getValue());
    }

    @Test
    public void shouldAddTaskFilteredActivity() {
        when(taskActivitiesDataService.findById(TASK_ACTIVITY_ID)).thenReturn(createInProgress());
        String messageKey = "task.filtered";

        ArgumentCaptor<TaskActivity> captor = ArgumentCaptor.forClass(TaskActivity.class);

        activityService.addTaskFiltered(TASK_ACTIVITY_ID);

        verify(taskActivitiesDataService).findById(TASK_ACTIVITY_ID);
        verify(taskActivitiesDataService).update(captor.capture());

        assertActivity(messageKey, Collections.<String>emptyList(), TASK_ID, TaskActivityType.FILTERED, null, null, captor.getValue());
    }

    @Test
    public void shouldAddTaskSuccessActivity() {
        when(taskActivitiesDataService.findById(TASK_ACTIVITY_ID)).thenReturn(createInProgress());
        String messageKey = "task.success.ok";

        ArgumentCaptor<TaskActivity> captor = ArgumentCaptor.forClass(TaskActivity.class);

        activityService.addSuccessfulExecution(TASK_ACTIVITY_ID);

        verify(taskActivitiesDataService).findById(TASK_ACTIVITY_ID);
        verify(taskActivitiesDataService).update(captor.capture());

        TaskActivity activity = captor.getValue();

        assertEquals(1, activity.getTaskExecutionProgress().getActionsSucceeded());
        assertActivity(messageKey, Collections.<String>emptyList(), TASK_ID, TaskActivityType.SUCCESS, null, null, activity);
    }

    @Test
    public void shouldAddTaskWarningActivity() {
        String messageKey = "task.warning.taskDisabled";

        ArgumentCaptor<TaskActivity> captor = ArgumentCaptor.forClass(TaskActivity.class);

        activityService.addTaskDisabledWarning(task);

        verify(taskActivitiesDataService).create(captor.capture());

        assertActivity(messageKey, Collections.<String>emptyList(), TASK_ID,
                TaskActivityType.WARNING, null, null, captor.getValue());
    }

    @Test
    public void shouldAddTaskWarningActivityWithGivenKeyAndField() {
        String messageKey = "warning.manipulation";

        ArgumentCaptor<TaskActivity> captor = ArgumentCaptor.forClass(TaskActivity.class);

        activityService.addWarning(task, messageKey, ERROR_FIELD.get(0));

        verify(taskActivitiesDataService).create(captor.capture());

        assertActivity(messageKey, ERROR_FIELD, TASK_ID, TaskActivityType.WARNING, null, null, captor.getValue());
    }

    @Test
    public void shouldAddTaskWarningActivityWithGivenException() {
        TaskHandlerException exception = new TaskHandlerException(TRIGGER, "trigger.exception", new TaskHandlerException(TRIGGER, "task.exception"));
        String messageKey = "warning.manipulation";

        ArgumentCaptor<TaskActivity> captor = ArgumentCaptor.forClass(TaskActivity.class);

        activityService.addWarningWithException(task, messageKey, ERROR_FIELD.get(0), exception);

        verify(taskActivitiesDataService).create(captor.capture());

        assertActivity(messageKey, ERROR_FIELD, TASK_ID, TaskActivityType.WARNING, getStackTrace(exception.getCause()), null, captor.getValue());
    }

    @Test
    public void shouldDeleteAllTaskActivitiesForGivenTask() {
        when(taskActivitiesDataService.byTask(TASK_ID)).thenReturn(activities);

        activityService.deleteActivitiesForTask(TASK_ID);

        verify(taskActivitiesDataService, times(activities.size())).delete(any(TaskActivity.class));
    }

    @Test
    public void shouldNotRemoveAnyActivitiesWhenTaskHasNotActivities() {
        when(taskActivitiesDataService.byTask(TASK_ID)).thenReturn(new ArrayList<>());

        activityService.deleteActivitiesForTask(TASK_ID);

        verify(taskActivitiesDataService, never()).delete(any(TaskActivity.class));
    }

    @Test
    public void shouldReturnPaginatedActivitiesForGivenTask() {
        Set<TaskActivityType> types = new HashSet<>();
        types.addAll(Arrays.asList(TaskActivityType.values()));
        Set<Long> activitiesIds = new HashSet<>();

        QueryParams queryParams = new QueryParams((Order) null);
        boolean lastExecution = false;
        when(taskActivitiesDataService.byTaskAndActivityTypesAndIds(TASK_ID, types, activitiesIds, queryParams)).thenReturn(activities);

        List<TaskActivity> actual = activityService.getTaskActivities(TASK_ID, types, queryParams, lastExecution);

        assertNotNull(actual);
        assertEquals(activities, actual);
    }

    private void assertActivity(String messageKey, List<String> field, Long taskId, TaskActivityType activityType,
                                String stackTraceElement, Map<String, Object> errorParams, TaskActivity activity) {
        assertNotNull(activity);

        assertEquals(messageKey, activity.getMessage());
        assertEquals(taskId, activity.getTask());
        assertEquals(activityType, activity.getActivityType());
        assertEquals(stackTraceElement, activity.getStackTraceElement());
        assertEquals(errorParams, activity.getParameters());

        assertEquals(field, activity.getFields());
    }

    private List<TaskActivity> createTaskActivities() {
        List<TaskActivity> messages = new ArrayList<>();
        messages.add(createError());
        messages.add(createError());
        messages.add(createSuccess());
        messages.add(createError());
        messages.add(createError());
        messages.add(createWarning());
        messages.add(createSuccess());
        messages.add(createError());
        messages.add(createError());
        messages.add(createError());
        messages.add(createError());
        messages.add(createInProgress());

        return messages;
    }

    private TaskActivity createInProgress() {
        return new TaskActivity("", new ArrayList<>(), TASK_ID, TaskActivityType.IN_PROGRESS, new TaskExecutionProgress(1));
    }

    private TaskActivity createError() {
        return new TaskActivity(ERROR.getValue(), ERROR_FIELD, TASK_ID, ERROR, new TaskExecutionProgress(1));
    }

    private TaskActivity createSuccess() {
        return new TaskActivity(SUCCESS.getValue(), TASK_ID, SUCCESS);
    }

    private TaskActivity createWarning() {
        return new TaskActivity(WARNING.getValue(), TASK_ID, WARNING);
    }
}
