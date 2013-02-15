package org.motechproject.tasks.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commons.api.DataProvider;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.motechproject.tasks.service.TaskTriggerHandler;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ManagementDataProviderTest {

    @Mock
    TaskTriggerHandler taskTriggerHandler;

    @Mock
    TaskDataProviderService taskDataProviderService;

    @Mock
    DataProvider dataProvider;

    ManagementDataProvider managementDataProvider;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        managementDataProvider = new ManagementDataProvider(taskTriggerHandler, taskDataProviderService);
    }

    @Test
    public void testBind() throws IOException {
        TaskDataProvider taskDataProvider = new TaskDataProvider();
        taskDataProvider.setId("12345");

        String body = taskDataProvider.toString();

        when(dataProvider.toJSON()).thenReturn(body);
        when(taskDataProviderService.registerProvider(body)).thenReturn(taskDataProvider);

        managementDataProvider.bind(dataProvider, null);

        verify(taskDataProviderService).registerProvider(body);
        verify(taskTriggerHandler).addDataProvider(taskDataProvider.getId(), dataProvider);
    }

    @Test
    public void testBindNoRegistered() throws IOException {
        managementDataProvider.bind(new Object(), null);

        verify(taskDataProviderService, never()).registerProvider(anyString());
        verify(taskTriggerHandler, never()).addDataProvider(anyString(), any(DataProvider.class));
    }
}
