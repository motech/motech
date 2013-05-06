package org.motechproject.tasks.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tasks.domain.TaskActivity;
import org.motechproject.tasks.service.TaskActivityService;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.domain.TaskActivityType.ERROR;
import static org.motechproject.tasks.domain.TaskActivityType.SUCCESS;
import static org.motechproject.tasks.domain.TaskActivityType.WARNING;

public class ActivityControllerTest {
    private static final String TASK_ID = "12345";

    @Mock
    TaskActivityService activityService;

    ActivityController controller;

    List<TaskActivity> expected;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        controller = new ActivityController(activityService);

        expected = new ArrayList<>();
        expected.add(new TaskActivity(SUCCESS.getValue(), TASK_ID, SUCCESS));
        expected.add(new TaskActivity(WARNING.getValue(), TASK_ID, WARNING));
        expected.add(new TaskActivity(ERROR.getValue(), TASK_ID, ERROR));
    }

    @Test
    public void shouldGetAllActivities() {
        when(activityService.getAllActivities()).thenReturn(expected);

        List<TaskActivity> actual = controller.getAllActivities();

        verify(activityService).getAllActivities();
        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetTaskActivities() {
        when(activityService.getTaskActivities(TASK_ID)).thenReturn(expected);

        List<TaskActivity> actual = controller.getTaskActivities(TASK_ID);

        verify(activityService).getTaskActivities(TASK_ID);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldRemoveAllActivitiesForTask() {
        controller.deleteActivitiesForTask(TASK_ID);
        verify(activityService).deleteActivitiesForTask(TASK_ID);
    }
}
