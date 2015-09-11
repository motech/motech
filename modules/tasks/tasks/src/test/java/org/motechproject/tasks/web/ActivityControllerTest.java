package org.motechproject.tasks.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.tasks.domain.TaskActivity;
import org.motechproject.tasks.domain.TaskActivityType;
import org.motechproject.tasks.service.TaskActivityService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.domain.TaskActivityType.ERROR;
import static org.motechproject.tasks.domain.TaskActivityType.SUCCESS;
import static org.motechproject.tasks.domain.TaskActivityType.WARNING;

public class ActivityControllerTest {

    private static final long TASK_ID = 12345L;

    @Mock
    TaskActivityService activityService;

    ActivityController controller;

    List<TaskActivity> expected;
    Set<TaskActivityType> activityTypes;
    QueryParams queryParams;

    Integer page = 1;
    Integer pageSize = 10;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        controller = new ActivityController(activityService);

        expected = new ArrayList<>();
        expected.add(new TaskActivity(SUCCESS.getValue(), TASK_ID, SUCCESS));
        expected.add(new TaskActivity(WARNING.getValue(), TASK_ID, WARNING));
        expected.add(new TaskActivity(ERROR.getValue(), TASK_ID, ERROR));

        activityTypes = new HashSet<>();
        activityTypes.addAll(Arrays.asList(TaskActivityType.values()));

        queryParams = new QueryParams(page, pageSize);
    }

    @Test
    public void shouldGetAllLatestActivities() {
        when(activityService.getLatestActivities()).thenReturn(expected);

        List<TaskActivity> actual = controller.getRecentActivities();

        verify(activityService).getLatestActivities();
        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetTaskActivities() {
        when(activityService.getTaskActivities(eq(TASK_ID), anySet(), any(QueryParams.class))).thenReturn(expected);
        GridSettings settings = new GridSettings();
        settings.setPage(page);
        settings.setRows(pageSize);

        TaskActivityRecords actual = controller.getTaskActivities(TASK_ID, settings);

        verify(activityService).getTaskActivities(eq(TASK_ID), anySet(), any(QueryParams.class));
        assertEquals(expected, actual.getRows());
        assertEquals(page, actual.getPage());
    }

    @Test
    public void shouldRemoveAllActivitiesForTask() {
        controller.deleteActivitiesForTask(TASK_ID);
        verify(activityService).deleteActivitiesForTask(TASK_ID);
    }
}
