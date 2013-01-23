package org.motechproject.tasks.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commons.api.DataProvider;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.service.TaskDataProviderService;

import java.io.IOException;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DataProviderRegisterTest {

    @Mock
    TaskDataProviderService taskDataProviderService;

    @Mock
    DataProvider dataProvider;

    DataProviderRegister dataProviderRegister;

    String body = new TaskDataProvider().toString();

    @Before
    public void setup() throws Exception {
        initMocks(this);

        dataProviderRegister = new DataProviderRegister(taskDataProviderService);
    }

    @Test
    public void shouldRegisterProviderWhenDataProviderServiceIsAvailable() throws IOException {
        when(dataProvider.toJSON()).thenReturn(body);

        dataProviderRegister.bind(dataProvider, null);

        verify(taskDataProviderService).registerProvider(body);
    }

    @Test
    public void shouldNotRegisterProviderWhenGotOtherServices() throws IOException {
        when(dataProvider.toJSON()).thenReturn(body);

        dataProviderRegister.bind(new Object(), null);

        verify(taskDataProviderService, never()).registerProvider(body);
    }
}
