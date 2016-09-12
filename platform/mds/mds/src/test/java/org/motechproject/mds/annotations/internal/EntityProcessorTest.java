package org.motechproject.mds.annotations.internal;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.internal.samples.*;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.dto.TrackingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.testutil.MockBundle;
import org.motechproject.mds.util.SecurityMode;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import java.io.File;
import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EntityProcessorTest extends MockBundle {

    @Spy
    private Bundle bundle = new org.eclipse.gemini.blueprint.mock.MockBundle();

    @Mock
    private SchemaHolder schemaHolder;

    @Mock
    private FieldProcessor fieldProcessor;

    @Mock
    private UIFilterableProcessor uiFilterableProcessor;

    @Mock
    private UIDisplayableProcessor uiDisplayableProcessor;

    @Mock
    private RestOperationsProcessor restOperationsProcessor;

    @Mock
    private RestIgnoreProcessor restIgnoreProcessor;

    @Mock
    private CrudEventsProcessor crudEventsProcessor;

    @Mock
    private NonEditableProcessor nonEditableProcessor;

    @Mock
    private BundleWiring bundleWiring;

    @Captor
    private ArgumentCaptor<EntityDto> captor;

    @Captor
    private ArgumentCaptor<TrackingDto> trackingDtoCaptor;

    private EntityProcessor processor;

    EntityDto entity = new EntityDto(
            null, AnotherSample.class.getName(), "test", "mds", null, null, false,

            SecurityMode.EVERYONE, null, null, null, null, false, false, null);

    EntityDto readOnlyEntity = new EntityDto(
            null, ReadAccessSample.class.getName(), "foo", "mds", null, null, false,
            null, null, SecurityMode.EVERYONE, null, null, false, false, null);

    @Before
    public void setUp() throws Exception {
        processor = new EntityProcessor();
        processor.setFieldProcessor(fieldProcessor);
        processor.setUIFilterableProcessor(uiFilterableProcessor);
        processor.setUIDisplayableProcessor(uiDisplayableProcessor);
        processor.setRestOperationsProcessor(restOperationsProcessor);
        processor.setRestIgnoreProcessor(restIgnoreProcessor);
        processor.setCrudEventsProcessor(crudEventsProcessor);
        processor.setNonEditableProcessor(nonEditableProcessor);
        processor.setBundle(bundle);
        processor.setSchemaHolder(schemaHolder);
        processor.beforeExecution();

        when(schemaHolder.getType(Long.class)).thenReturn(TypeDto.LONG);
        when(schemaHolder.getType(String.class)).thenReturn(TypeDto.STRING);
        when(schemaHolder.getType(DateTime.class)).thenReturn(TypeDto.DATETIME);

        when(schemaHolder.getEntityByClassName(AnotherSample.class.getName())).thenReturn(entity);

        when(schemaHolder.getEntityByClassName(ReadAccessSample.class.getName())).thenReturn(readOnlyEntity);

        when(schemaHolder.getAdvancedSettings(anyString())).thenReturn(new AdvancedSettingsDto());

        setUpMockBundle();
    }

    @Test
    public void shouldReturnCorrectAnnotation() throws Exception {
        assertEquals(Entity.class, processor.getAnnotationType());
    }

    @Test
    public void shouldReturnCorrectElementList() throws Exception {
        File file = computeTestDataRoot(getClass());
        String location = file.toURI().toURL().toString();

        doReturn(location).when(bundle).getLocation();
        doReturn(Sample.class).when(bundle).loadClass(Sample.class.getName());
        doReturn(bundleWiring).when(bundle).adapt(BundleWiring.class);

        Set<? extends AnnotatedElement> actual = processor.getElementsToProcess();

        assertEquals(8, actual.size());
        assertContainsClass(actual, Sample.class.getName());
        assertContainsClass(actual, RelatedSample.class.getName());
        assertContainsClass(actual, AnotherSample.class.getName());
    }

    @Test
    public void shouldProcessClassWithEntityAnnotation() throws Exception {
        processor.process(Sample.class);

        verify(fieldProcessor).setClazz(Sample.class);
        verify(fieldProcessor).setEntity(any(EntityDto.class));
        verify(fieldProcessor).execute(bundle, schemaHolder);

        verify(uiFilterableProcessor).setClazz(Sample.class);
        verify(uiFilterableProcessor).execute(bundle, schemaHolder);

        verify(uiDisplayableProcessor).setClazz(Sample.class);
        verify(uiDisplayableProcessor).execute(bundle, schemaHolder);

        verify(nonEditableProcessor).setClazz(Sample.class);
        verify(nonEditableProcessor).execute(bundle, schemaHolder);
    }

    @Test
    public void shouldNotProcessClassWithEntityExtensionAnnotation() throws Exception {
        processor.process(ExtendedSample.class);

        verify(fieldProcessor, never()).setClazz(ExtendedSample.class);
        verify(fieldProcessor, never()).setEntity(any(EntityDto.class));
        verify(fieldProcessor, never()).execute(bundle, schemaHolder);

        verify(uiFilterableProcessor, never()).setClazz(ExtendedSample.class);
        verify(uiFilterableProcessor, never()).execute(bundle, schemaHolder);

        verify(uiDisplayableProcessor, never()).setClazz(ExtendedSample.class);
        verify(uiDisplayableProcessor, never()).execute(bundle, schemaHolder);

        verify(nonEditableProcessor, never()).setClazz(ExtendedSample.class);
        verify(nonEditableProcessor, never()).execute(bundle, schemaHolder);
    }

    @Test
    public void shouldSetRecordHistoryFlag() {
        processor.process(AnotherSample.class);

        verify(crudEventsProcessor).setClazz(AnotherSample.class);
        verify(crudEventsProcessor).setTrackingDto(trackingDtoCaptor.capture());
        verify(crudEventsProcessor).execute(bundle, schemaHolder);

        TrackingDto trackingDto = trackingDtoCaptor.getValue();
        assertFalse(trackingDto.isRecordHistory());

        processor.process(Sample.class);

        verify(crudEventsProcessor).setClazz(Sample.class);
        verify(crudEventsProcessor, times(2)).setTrackingDto(trackingDtoCaptor.capture());
        verify(crudEventsProcessor, times(2)).execute(bundle, schemaHolder);

        trackingDto = trackingDtoCaptor.getValue();
        assertTrue(trackingDto.isRecordHistory());
    }

    @Test
    public void shouldSetSecurityOptions() {
        assertNotSame(SecurityMode.USERS, entity.getSecurityMode());
        assertEquals(0, entity.getSecurityMembers().size());

        processor.process(AnotherSample.class);

        assertEquals(SecurityMode.USERS, entity.getSecurityMode());
        assertTrue(entity.getSecurityMembers().contains("motech"));
    }

    @Test
    public void shouldSetReadOnlySecurityOptions() {
        assertNotSame(SecurityMode.PERMISSIONS, readOnlyEntity.getReadOnlySecurityMode());
        assertEquals(0, readOnlyEntity.getSecurityMembers().size());

        processor.process(ReadAccessSample.class);

        assertEquals(SecurityMode.PERMISSIONS, readOnlyEntity.getReadOnlySecurityMode());
        assertTrue(readOnlyEntity.getReadOnlySecurityMembers().contains("manageEbodac"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailProcessingClassesWithInvalidSecurityOptions() {
        try {
            processor.process(InvalidSecuritySample.class);
        } finally {
            verifyZeroInteractions(fieldProcessor);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailProcessingClassesWithAnotherInvalidSecurityOptions() {
        try {
            processor.process(AnotherInvalidSecuritySample.class);
        } finally {
            verifyZeroInteractions(fieldProcessor);
        }
    }

    @Test
    public void shouldNotProcessClassWithoutAnnotation() throws Exception {
        processor.process(Object.class);

        verifyZeroInteractions(schemaHolder, fieldProcessor);
    }

    @Test
    public void shouldProcessFetchDepth() {
        processor.process(Sample.class);
        processor.process(RelatedSample.class);

        EntityDto entity = processor.getProcessingResult().get(0).getEntityProcessingResult();
        assertEquals(Integer.valueOf(3), entity.getMaxFetchDepth());

        entity = processor.getProcessingResult().get(1).getEntityProcessingResult();
        assertNull(entity.getMaxFetchDepth());
    }

    @Test
    public void shouldNotOverrideHistorySettingsIfModifiedByUser() {
        EntityDto existingEntity = new EntityDto(1L, Sample.class.getName());
        TrackingDto existingTracking = new TrackingDto();
        existingTracking.setModifiedByUser(true);
        AdvancedSettingsDto existingAdvancedSettings = new AdvancedSettingsDto();
        existingAdvancedSettings.setTracking(existingTracking);
        when(schemaHolder.getEntityByClassName(Sample.class.getName())).thenReturn(existingEntity);
        when(schemaHolder.getAdvancedSettings(Sample.class.getName())).thenReturn(existingAdvancedSettings);
        when(crudEventsProcessor.getProcessingResult()).thenReturn(existingTracking);

        processor.process(Sample.class);

        EntityDto entity = processor.getProcessingResult().get(0).getEntityProcessingResult();
        TrackingDto tracking = processor.getProcessingResult().get(0).getTrackingProcessingResult();

        assertFalse(entity.isRecordHistory());
        assertFalse(tracking.isRecordHistory());
    }

    @Test
    public void shouldSetNonEditableFlag() {
        processor.process(AnotherSample.class);

        verify(crudEventsProcessor).setClazz(AnotherSample.class);
        verify(crudEventsProcessor).setTrackingDto(trackingDtoCaptor.capture());
        verify(crudEventsProcessor).execute(bundle, schemaHolder);

        TrackingDto trackingDto = trackingDtoCaptor.getValue();
        assertTrue(trackingDto.isNonEditable());

        processor.process(Sample.class);

        verify(crudEventsProcessor).setClazz(Sample.class);
        verify(crudEventsProcessor, times(2)).setTrackingDto(trackingDtoCaptor.capture());
        verify(crudEventsProcessor, times(2)).execute(bundle, schemaHolder);

        trackingDto = trackingDtoCaptor.getValue();
        assertFalse(trackingDto.isNonEditable());
    }

    @Override
    protected Map<String, Class> getMappingsForLoader() {
        Map<String, Class> mappings = new LinkedHashMap<>();
        mappings.put(Sample.class.getName(), Sample.class);

        return mappings;
    }

    @Override
    protected Class getTestClass() {
        return getClass();
    }

    @Override
    protected Bundle getMockBundle() {
        return bundle;
    }

    private void assertContainsClass(Set<? extends AnnotatedElement> classes, String className) {
        for (AnnotatedElement element : classes) {
            Class<?> clazz = (Class<?>) element;
            if (clazz.getName().equals(className)) {
                return;
            }
        }
        fail("Class not found in list: " + className);
    }

}
