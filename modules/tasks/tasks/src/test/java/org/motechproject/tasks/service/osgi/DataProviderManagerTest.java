package org.motechproject.tasks.service.osgi;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commons.api.DataProvider;
import org.motechproject.tasks.domain.mds.task.TaskDataProvider;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.motechproject.tasks.service.impl.TaskTriggerHandler;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DataProviderManagerTest {
    private static final String TASK_DATA_PROVIDER_NAME = "test";
    private static final Long TASK_DATA_PROVIDER_ID = 12345L;

    @Mock
    TaskTriggerHandler taskTriggerHandler;

    @Mock
    TaskDataProviderService taskDataProviderService;

    @Mock
    DataProvider dataProvider;

    @Before
    public void setup() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldRegisterProvider() throws IOException {
        DataProviderManager mgr = new DataProviderManager(taskDataProviderService);
        TaskDataProvider provider = new TaskDataProvider();
        provider.setId(TASK_DATA_PROVIDER_ID);

        String json = String.format("{ name: '%s', objects: []", TASK_DATA_PROVIDER_NAME);

        when(dataProvider.toJSON()).thenReturn(json);

        mgr.bind(dataProvider, null);

        verify(taskDataProviderService).registerProvider(json);
        verify(taskTriggerHandler, never()).addDataProvider(dataProvider);
    }

    @Test
    public void shouldRegisterProviderAndAddToHandler() throws IOException {
        DataProviderManager mgr = new DataProviderManager(taskTriggerHandler, taskDataProviderService);
        TaskDataProvider provider = new TaskDataProvider();
        provider.setId(TASK_DATA_PROVIDER_ID);

        String json = String.format("{ name: '%s', objects: []", TASK_DATA_PROVIDER_NAME);

        when(dataProvider.toJSON()).thenReturn(json);

        mgr.bind(dataProvider, null);

        verify(taskDataProviderService).registerProvider(json);
        verify(taskTriggerHandler).addDataProvider(dataProvider);
    }

    @Test
    public void shouldRemoveProviderFromHandler() throws IOException {
        DataProviderManager mgr = new DataProviderManager(taskTriggerHandler, taskDataProviderService);
        TaskDataProvider provider = new TaskDataProvider();
        provider.setId(TASK_DATA_PROVIDER_ID);

        when(dataProvider.getName()).thenReturn(TASK_DATA_PROVIDER_NAME);
        when(taskDataProviderService.getProvider(TASK_DATA_PROVIDER_NAME)).thenReturn(provider);

        mgr.unbind(dataProvider, null);

        verify(taskTriggerHandler).removeDataProvider(TASK_DATA_PROVIDER_NAME);
    }

    @Test
    public void shouldNotRemoveProviderFromHandler() throws IOException {
        DataProviderManager mgr = new DataProviderManager(taskDataProviderService);
        TaskDataProvider provider = new TaskDataProvider();
        provider.setId(TASK_DATA_PROVIDER_ID);

        when(taskDataProviderService.getProvider(TASK_DATA_PROVIDER_NAME)).thenReturn(provider);

        mgr.unbind(dataProvider, null);

        verify(taskTriggerHandler, never()).removeDataProvider(TASK_DATA_PROVIDER_NAME);
    }

    @Test
    public void shouldNotBindForObjectOtherThanDataProvider() throws IOException {
        DataProviderManager mgr = new DataProviderManager(taskTriggerHandler, taskDataProviderService);

        mgr.bind(new Object(), null);

        verify(taskDataProviderService, never()).registerProvider(anyString());
        verify(taskTriggerHandler, never()).addDataProvider(any(DataProvider.class));
    }

    @Test
    public void shouldNotUnbindForObjectOtherThanDataProvider() throws IOException {
        DataProviderManager mgr = new DataProviderManager(taskTriggerHandler, taskDataProviderService);

        mgr.unbind(new Object(), null);

        verify(taskDataProviderService, never()).getProvider(anyString());
        verify(taskTriggerHandler, never()).removeDataProvider(anyString());
    }
}
