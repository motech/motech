package org.motechproject.tasks.service.impl;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.domain.TaskDataProviderObject;
import org.motechproject.tasks.repository.AllTaskDataProviders;
import org.motechproject.tasks.service.TaskDataProviderService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TaskDataProviderServiceImplTest {
    private static final String PROVIDER_NAME = "test";

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
    public void shouldRegisterProviderFromInputStream() {
        Type type = new TypeToken<TaskDataProvider>() { }.getType();
        TaskDataProvider provider = new TaskDataProvider();

        when(motechJsonReader.readFromStream(inputStream, type)).thenReturn(provider);

        taskDataProviderService.registerProvider(inputStream);

        verify(motechJsonReader).readFromStream(inputStream, type);
        verify(allTaskDataProviders).addOrUpdate(provider);
    }

    @Test
    public void shouldRegisterProviderFromString() throws IOException {
        String json = String.format("{ name: '%s', objects: [] }", PROVIDER_NAME);
        Type type = new TypeToken<TaskDataProvider>() { }.getType();
        TaskDataProvider provider = new TaskDataProvider();
        StringWriter writer = new StringWriter();

        when(motechJsonReader.readFromStream(any(InputStream.class), eq(type))).thenReturn(provider);

        taskDataProviderService.registerProvider(json);
        ArgumentCaptor<InputStream> captor = ArgumentCaptor.forClass(InputStream.class);

        verify(motechJsonReader).readFromStream(captor.capture(), eq(type));
        verify(allTaskDataProviders).addOrUpdate(provider);

        InputStream value = captor.getValue();
        IOUtils.copy(value, writer);

        assertTrue(value instanceof ByteArrayInputStream);
        assertEquals(json, writer.toString());
    }

    @Test
    public void shouldGetProviderByName() {
        TaskDataProvider provider = new TaskDataProvider(PROVIDER_NAME, new ArrayList<TaskDataProviderObject>());

        when(allTaskDataProviders.byName(PROVIDER_NAME)).thenReturn(provider);

        assertEquals(provider, taskDataProviderService.getProvider(PROVIDER_NAME));
    }

    @Test
    public void shouldGetAllProviders() {
        List<TaskDataProvider> expected = new ArrayList<>();
        expected.add(new TaskDataProvider());
        expected.add(new TaskDataProvider());

        when(allTaskDataProviders.getAll()).thenReturn(expected);

        assertEquals(expected, taskDataProviderService.getProviders());
    }

}
