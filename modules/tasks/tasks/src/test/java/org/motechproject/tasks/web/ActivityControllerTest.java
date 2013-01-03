package org.motechproject.tasks.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tasks.domain.TaskActivity;
import org.motechproject.tasks.service.TaskActivityService;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
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
        verifyList(expected, actual);
    }

    @Test
    public void shouldGetTaskActivities() {
        when(activityService.getTaskActivities(TASK_ID)).thenReturn(expected);

        List<TaskActivity> actual = controller.getTaskActivities(TASK_ID);

        verify(activityService).getTaskActivities(TASK_ID);
        verifyList(expected, actual);
    }

    private void verifyList(List<TaskActivity> expected, List<TaskActivity> actual) {
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); ++i) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

}
