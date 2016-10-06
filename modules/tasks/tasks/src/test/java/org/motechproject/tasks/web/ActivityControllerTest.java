package org.motechproject.tasks.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.enums.TaskActivityType;
import org.motechproject.tasks.dto.TaskActivityDto;
import org.motechproject.tasks.service.TaskActivityService;
import org.motechproject.tasks.service.TaskWebService;
import org.motechproject.tasks.service.impl.TaskTriggerHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.domain.enums.TaskActivityType.ERROR;
import static org.motechproject.tasks.domain.enums.TaskActivityType.SUCCESS;
import static org.motechproject.tasks.domain.enums.TaskActivityType.WARNING;

public class ActivityControllerTest {

    private static final long TASK_ID = 12345L;
    private static final long ACTIVITY_ID = 54321L;

    @Mock
    TaskActivityService activityService;

    @Mock
    TaskTriggerHandler taskTriggerHandler;

    @Mock
    TaskWebService taskWebService;

    ActivityController controller;

    Task task;
    List<TaskActivityDto> expected;
    Set<TaskActivityType> activityTypes;
    QueryParams queryParams;
    Map<String, Object> params;

    Integer page = 1;
    Integer pageSize = 10;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        controller = new ActivityController(activityService, taskTriggerHandler, taskWebService);

        params = new HashMap<String, Object>();
        params.put("errorKey", "errorValue");

        expected = new ArrayList<>();
        expected.add(new TaskActivityDto(SUCCESS.getValue(), TASK_ID, SUCCESS));
        expected.add(new TaskActivityDto(WARNING.getValue(), TASK_ID, WARNING));
        expected.add(new TaskActivityDto(ERROR.getValue(), TASK_ID, ERROR));
        expected.add(new TaskActivityDto(ACTIVITY_ID, ERROR.getValue(), TASK_ID, new ArrayList<>(), null, ERROR, null, params));

        activityTypes = new HashSet<>();
        activityTypes.addAll(Arrays.asList(TaskActivityType.values()));

        queryParams = new QueryParams(page, pageSize);

        task = new Task();
        task.setId(TASK_ID);
    }

    @Test
    public void shouldGetAllLatestActivities() {
        when(taskWebService.getLatestActivities()).thenReturn(expected);

        List<TaskActivityDto> actual = controller.getRecentActivities();

        verify(taskWebService).getLatestActivities();
        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetTaskActivities() {
        when(taskWebService.getTaskActivities(eq(TASK_ID), anySet(), any(QueryParams.class))).thenReturn(expected);
        GridSettings settings = new GridSettings();
        settings.setPage(page);
        settings.setRows(pageSize);

        TaskActivityRecords actual = controller.getTaskActivities(TASK_ID, settings);

        verify(taskWebService).getTaskActivities(eq(TASK_ID), anySet(), any(QueryParams.class));
        assertEquals(expected, actual.getRows());
        assertEquals(page, actual.getPage());
    }

    @Test
    public void shouldRemoveAllActivitiesForTask() {
        controller.deleteActivitiesForTask(TASK_ID);
        verify(activityService).deleteActivitiesForTask(TASK_ID);
    }

    @Test
    public void shouldRetryTask() throws InterruptedException {
        controller.retryTask(ACTIVITY_ID);
        //Wait for start of the new thread in which retry is run
        Thread.sleep(1000);
        verify(taskTriggerHandler).retryTask(ACTIVITY_ID);
    }
}
