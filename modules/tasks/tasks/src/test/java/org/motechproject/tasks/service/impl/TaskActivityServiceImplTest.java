package org.motechproject.tasks.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActivity;
import org.motechproject.tasks.domain.TaskActivityType;
import org.motechproject.tasks.repository.AllTaskActivities;
import org.motechproject.tasks.service.TaskActivityService;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.domain.TaskActivityType.ERROR;
import static org.motechproject.tasks.domain.TaskActivityType.SUCCESS;
import static org.motechproject.tasks.domain.TaskActivityType.WARNING;

public class TaskActivityServiceImplTest {
    private static final String TASK_ID = "12345";

    @Mock
    AllTaskActivities allTaskActivities;

    TaskActivityService activityService;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        activityService = new TaskActivityServiceImpl(allTaskActivities);
    }

    @Test
    public void shouldReturnTaskActivitiesForTaskFromLastErrorActivity() {
        when(allTaskActivities.byTaskId(TASK_ID)).thenReturn(getTaskActivities());

        Task t = new Task();
        t.setId(TASK_ID);

        List<TaskActivity> errors = activityService.errorsFromLastRun(t);

        assertNotNull(errors);
        assertEquals(4, errors.size());

        for (TaskActivity error : errors) {
            assertEquals(ERROR.getValue(), error.getMessage());
            assertEquals(TASK_ID, error.getTask());
            assertEquals(ERROR, error.getActivityType());
        }
    }

    @Test
    public void shouldReturnEmptyListWhenTaskHasNotActivities() {
        when(allTaskActivities.byTaskId(TASK_ID)).thenReturn(new ArrayList<TaskActivity>());

        Task t = new Task();
        t.setId(TASK_ID);

        List<TaskActivity> errors = activityService.errorsFromLastRun(t);

        assertNotNull(errors);
        assertEquals(0, errors.size());
    }

    @Test
    public void shouldAddTaskErrorActivity() {
        Task t = new Task();
        t.setId(TASK_ID);

        String message = "error.notFoundTrigger";

        ArgumentCaptor<TaskActivity> captor = ArgumentCaptor.forClass(TaskActivity.class);

        activityService.addError(t, message);

        verify(allTaskActivities).add(captor.capture());

        assertActivity(message, TASK_ID, TaskActivityType.ERROR, captor.getValue());
    }

    @Test
    public void shouldAddTaskSuccessActivity() {
        Task t = new Task();
        t.setId(TASK_ID);

        String message = "success.ok";

        ArgumentCaptor<TaskActivity> captor = ArgumentCaptor.forClass(TaskActivity.class);

        activityService.addSuccess(t);

        verify(allTaskActivities).add(captor.capture());

        assertActivity(message, TASK_ID, TaskActivityType.SUCCESS, captor.getValue());
    }

    @Test
    public void shouldAddTaskWarningActivity() {
        Task t = new Task();
        t.setId(TASK_ID);

        String message = "warning.taskDisabled";

        ArgumentCaptor<TaskActivity> captor = ArgumentCaptor.forClass(TaskActivity.class);

        activityService.addWarning(t);

        verify(allTaskActivities).add(captor.capture());

        assertActivity(message, TASK_ID, TaskActivityType.WARNING, captor.getValue());
    }

    @Test
    public void shouldDeleteAllTaskActivitiesForGivenTask() {
        List<TaskActivity> activities = getTaskActivities();
        when(allTaskActivities.byTaskId(TASK_ID)).thenReturn(activities);

        activityService.deleteActivitiesForTask(TASK_ID);

        verify(allTaskActivities, times(activities.size())).remove(any(TaskActivity.class));
    }

    @Test
    public void shouldNotRemoveAnyActivitiesWhenTaskHasNotActivities() {
        when(allTaskActivities.byTaskId(TASK_ID)).thenReturn(new ArrayList<TaskActivity>());

        activityService.deleteActivitiesForTask(TASK_ID);

        verify(allTaskActivities, never()).remove(any(TaskActivity.class));
    }

    @Test
    public void shouldReturnAllActivities() {
        List<TaskActivity> expected = getTaskActivities();

        when(allTaskActivities.getAll()).thenReturn(expected);

        List<TaskActivity> actual = activityService.getAllActivities();

        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); ++i) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    private void assertActivity(String message, String task, TaskActivityType activityType, TaskActivity activity) {
        assertNotNull(activity);
        assertEquals(message, activity.getMessage());
        assertEquals(task, activity.getTask());
        assertEquals(activityType, activity.getActivityType());
    }

    private List<TaskActivity> getTaskActivities() {
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

        return messages;
    }

    private TaskActivity createError() {
        return new TaskActivity(ERROR.getValue(), TASK_ID, ERROR);
    }

    private TaskActivity createSuccess() {
        return new TaskActivity(SUCCESS.getValue(), TASK_ID, SUCCESS);
    }

    private TaskActivity createWarning() {
        return new TaskActivity(WARNING.getValue(), TASK_ID, WARNING);
    }
}
