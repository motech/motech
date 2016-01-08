package org.motechproject.tasks.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.service.TaskDataProviderService;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TaskDataProviderControllerTest {

    @Mock
    private TaskDataProviderService taskDataProviderService;

    private TaskDataProviderController controller;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        controller = new TaskDataProviderController(taskDataProviderService);
    }

    @Test
    public void testGetAllDataProviders() throws Exception {
        List<TaskDataProvider> expected = new ArrayList<>();

        when(taskDataProviderService.getProviders()).thenReturn(expected);

        List<TaskDataProvider> actual = controller.getAllDataProviders();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}
