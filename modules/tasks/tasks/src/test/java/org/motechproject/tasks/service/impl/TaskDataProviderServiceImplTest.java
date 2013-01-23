package org.motechproject.tasks.service.impl;

import com.google.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.repository.AllTaskDataProviders;
import org.motechproject.tasks.service.TaskDataProviderService;

import java.io.InputStream;
import java.lang.reflect.Type;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TaskDataProviderServiceImplTest {
    @Mock
    AllTaskDataProviders allTaskDataProviders;

    @Mock
    InputStream inputStream;

    @Mock
    MotechJsonReader motechJsonReader;

    TaskDataProviderService taskDataProviderService;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        taskDataProviderService = new TaskDataProviderServiceImpl(allTaskDataProviders, motechJsonReader);
    }

    @Test
    public void shouldRegisterProvider() {
        Type type = new TypeToken<TaskDataProvider>() { }.getType();
        TaskDataProvider provider = new TaskDataProvider();

        when(motechJsonReader.readFromStream(inputStream, type)).thenReturn(provider);

        taskDataProviderService.registerProvider(inputStream);

        verify(motechJsonReader).readFromStream(inputStream, type);
        verify(allTaskDataProviders).addOrUpdate(provider);
    }

}
