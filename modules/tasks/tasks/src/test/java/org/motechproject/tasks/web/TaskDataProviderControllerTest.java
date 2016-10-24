package org.motechproject.tasks.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tasks.dto.TaskDataProviderDto;
import org.motechproject.tasks.service.TaskWebService;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TaskDataProviderControllerTest {

    @Mock
    private TaskWebService taskWebService;

    private TaskDataProviderController controller;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        controller = new TaskDataProviderController(taskWebService);
    }

    @Test
    public void testGetAllDataProviders() throws Exception {
        List<TaskDataProviderDto> expected = new ArrayList<>();

        when(taskWebService.getProviders()).thenReturn(expected);

        List<TaskDataProviderDto> actual = controller.getAllDataProviders();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}
