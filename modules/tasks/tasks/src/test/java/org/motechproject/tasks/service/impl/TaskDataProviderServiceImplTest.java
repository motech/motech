package org.motechproject.tasks.service.impl;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.tasks.domain.mds.task.FieldParameter;
import org.motechproject.tasks.domain.mds.task.LookupFieldsParameter;
import org.motechproject.tasks.domain.mds.task.TaskDataProvider;
import org.motechproject.tasks.domain.mds.task.TaskDataProviderObject;
import org.motechproject.tasks.exception.ValidationException;
import org.motechproject.tasks.repository.DataProviderDataService;
import org.motechproject.tasks.service.TaskDataProviderService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.constants.EventDataKeys.DATA_PROVIDER_NAME;
import static org.motechproject.tasks.constants.EventSubjects.DATA_PROVIDER_UPDATE_SUBJECT;

public class TaskDataProviderServiceImplTest {
    private static final Long PROVIDER_ID = 12345L;
    private static final String PROVIDER_NAME = "test";

    @Mock
    DataProviderDataService dataProviderDataService;

    @Mock
    InputStream inputStream;

    @Mock
    MotechJsonReader motechJsonReader;

    @Mock
    EventRelay eventRelay;

    TaskDataProviderService taskDataProviderService;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        taskDataProviderService = new TaskDataProviderServiceImpl(eventRelay, motechJsonReader);
        ((TaskDataProviderServiceImpl) taskDataProviderService).bind(dataProviderDataService, Collections.emptyMap());
    }

    @Test(expected = ValidationException.class)
    public void shouldNotSaveProviderWhenValidationExceptionIsAppeared() {
        Type type = new TypeToken<TaskDataProvider>() {
        }.getType();
        TaskDataProvider provider = new TaskDataProvider();

        when(motechJsonReader.readFromStream(inputStream, type)).thenReturn(provider);

        taskDataProviderService.registerProvider(inputStream);

        verify(motechJsonReader).readFromStream(inputStream, type);
        verify(dataProviderDataService).create(provider);
    }

    @Test
    public void shouldRegisterProviderFromInputStream() {
        Type type = new TypeToken<TaskDataProvider>() {
        }.getType();
        List<TaskDataProviderObject> objects = new ArrayList<>();

        List<LookupFieldsParameter> lookupFields = asList(new LookupFieldsParameter("lookupField",asList("lookupField")));
        List<FieldParameter> fields = asList(new FieldParameter("displayName", "fieldKey"));
        objects.add(new TaskDataProviderObject("displayName", "type", lookupFields, fields));

        TaskDataProvider provider = new TaskDataProvider(PROVIDER_NAME, objects);

        when(motechJsonReader.readFromStream(inputStream, type)).thenReturn(provider);

        taskDataProviderService.registerProvider(inputStream);

        verify(motechJsonReader).readFromStream(inputStream, type);
        verify(dataProviderDataService).create(provider);
    }

    @Test
    public void shouldRegisterProviderFromString() throws IOException {
        String fieldParameterAsJson = "{ displayName: 'displayName', fieldKey: 'fieldKey' }";
        String lookupFieldsAsJson = "['lookupField']";
        String objectAsJson = String.format("{ displayName: 'displayName', type: 'type', lookupFields: %s, fields: [%s]}", lookupFieldsAsJson, fieldParameterAsJson);
        String providerAsJson = String.format("{ name: '%s', objects: [%s] }", PROVIDER_NAME, objectAsJson);

        Type type = new TypeToken<TaskDataProvider>() {
        }.getType();
        StringWriter writer = new StringWriter();

        List<LookupFieldsParameter> lookupFields = asList(new LookupFieldsParameter("lookupField", asList("lookupField")));
        List<FieldParameter> fields = asList(new FieldParameter("displayName", "fieldKey"));

        List<TaskDataProviderObject> objects = new ArrayList<>();
        objects.add(new TaskDataProviderObject("displayName", "type", lookupFields, fields));

        TaskDataProvider provider = new TaskDataProvider(PROVIDER_NAME, objects);

        when(motechJsonReader.readFromStream(any(InputStream.class), eq(type))).thenReturn(provider);

        taskDataProviderService.registerProvider(providerAsJson);
        ArgumentCaptor<InputStream> captor = ArgumentCaptor.forClass(InputStream.class);

        verify(motechJsonReader).readFromStream(captor.capture(), eq(type));
        verify(dataProviderDataService).create(provider);

        InputStream value = captor.getValue();
        IOUtils.copy(value, writer);

        assertTrue(value instanceof ByteArrayInputStream);
        assertEquals(providerAsJson, writer.toString());
    }

    @Test
    public void shouldUnregisterDataProvider() {
        List<LookupFieldsParameter> lookupFields = asList(new LookupFieldsParameter("lookupField", asList("lookupField")));
        List<FieldParameter> fields = asList(new FieldParameter("displayName", "fieldKey"));

        List<TaskDataProviderObject> objects = new ArrayList<>();
        objects.add(new TaskDataProviderObject("displayName", "type", lookupFields, fields));

        TaskDataProvider provider = new TaskDataProvider(PROVIDER_NAME, objects);
        when(dataProviderDataService.findByName(PROVIDER_NAME)).thenReturn(provider);

        taskDataProviderService.unregister(PROVIDER_NAME);

        verify(dataProviderDataService).delete(provider);
    }

    @Test
    public void shouldSendEventWhenProviderWasUpdated() {
        Type type = new TypeToken<TaskDataProvider>() {}.getType();
        List<TaskDataProviderObject> objects = new ArrayList<>();

        List<LookupFieldsParameter> lookupFields = asList(new LookupFieldsParameter("lookupField",asList("lookupField")));
        List<FieldParameter> fields = asList(new FieldParameter("displayName", "fieldKey"));
        objects.add(new TaskDataProviderObject("displayName", "type", lookupFields, fields));
        TaskDataProvider provider = new TaskDataProvider(PROVIDER_NAME, objects);

        List<TaskDataProviderObject> updatedObjects = new ArrayList<>(objects);
        updatedObjects.add(new TaskDataProviderObject("displayName2", "type2", lookupFields, fields));
        TaskDataProvider updatedProvider = new TaskDataProvider(PROVIDER_NAME, updatedObjects);

        when(motechJsonReader.readFromStream(inputStream, type)).thenReturn(updatedProvider);
        when(dataProviderDataService.findByName(PROVIDER_NAME)).thenReturn(provider);

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);
        taskDataProviderService.registerProvider(inputStream);

        ArgumentCaptor<TaskDataProvider> taskDataProviderArgumentCaptor = ArgumentCaptor.forClass(TaskDataProvider.class);
        verify(dataProviderDataService).update(taskDataProviderArgumentCaptor.capture());
        assertEquals(PROVIDER_NAME, taskDataProviderArgumentCaptor.getValue().getName());

        verify(dataProviderDataService).update(provider);

        verify(eventRelay).sendEventMessage(captor.capture());

        MotechEvent event = captor.getValue();

        assertEquals(DATA_PROVIDER_UPDATE_SUBJECT, event.getSubject());
        assertEquals(PROVIDER_NAME, event.getParameters().get(DATA_PROVIDER_NAME));
    }

    @Test
    public void shouldGetProviderByName() {
        TaskDataProvider provider = new TaskDataProvider(PROVIDER_NAME, new ArrayList<TaskDataProviderObject>());

        when(dataProviderDataService.findByName(PROVIDER_NAME)).thenReturn(provider);

        assertEquals(provider, taskDataProviderService.getProvider(PROVIDER_NAME));
    }

    @Test
    public void shouldGetProviderById() {
        TaskDataProvider provider = new TaskDataProvider(PROVIDER_NAME, new ArrayList<TaskDataProviderObject>());

        when(dataProviderDataService.findById(PROVIDER_ID)).thenReturn(provider);

        assertEquals(provider, taskDataProviderService.getProviderById(PROVIDER_ID));
    }

    @Test
    public void shouldGetAllProviders() {
        List<TaskDataProvider> expected = new ArrayList<>();
        expected.add(new TaskDataProvider());
        expected.add(new TaskDataProvider());

        when(dataProviderDataService.retrieveAll()).thenReturn(expected);

        assertEquals(expected, taskDataProviderService.getProviders());
    }

}