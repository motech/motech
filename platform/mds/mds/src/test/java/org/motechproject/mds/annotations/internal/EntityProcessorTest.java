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
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.TrackingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.TypeService;
import org.motechproject.mds.testutil.MockBundle;
import org.motechproject.mds.util.SecurityMode;
import org.osgi.framework.Bundle;

import java.io.File;
import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EntityProcessorTest extends MockBundle {

    @Spy
    private Bundle bundle = new org.eclipse.gemini.blueprint.mock.MockBundle();

    @Mock
    private EntityService entityService;

    @Mock
    private TypeService typeService;

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

    @Captor
    private ArgumentCaptor<EntityDto> captor;

    @Captor
    private ArgumentCaptor<TrackingDto> trackingDtoCaptor;

    private EntityProcessor processor;

    EntityDto entity = new EntityDto(
            null, AnotherSample.class.getName(), "test", "mds", null, null, false,
            SecurityMode.EVERYONE, null, null, false, false);

    @Before
    public void setUp() throws Exception {
        processor = new EntityProcessor();
        processor.setEntityService(entityService);
        processor.setTypeService(typeService);
        processor.setFieldProcessor(fieldProcessor);
        processor.setUIFilterableProcessor(uiFilterableProcessor);
        processor.setUIDisplayableProcessor(uiDisplayableProcessor);
        processor.setRestOperationsProcessor(restOperationsProcessor);
        processor.setRestIgnoreProcessor(restIgnoreProcessor);
        processor.setCrudEventsProcessor(crudEventsProcessor);
        processor.setNonEditableProcessor(nonEditableProcessor);
        processor.setBundle(bundle);
        processor.beforeExecution();

        when(typeService.findType(Long.class)).thenReturn(TypeDto.LONG);
        when(typeService.findType(String.class)).thenReturn(TypeDto.STRING);
        when(typeService.findType(DateTime.class)).thenReturn(TypeDto.DATETIME);

        when(entityService.getEntityByClassName(AnotherSample.class.getName()))
            .thenReturn(entity);

        when(entityService.getAdvancedSettings(null, true)).thenReturn(
                new AdvancedSettingsDto()
        );

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

        Set<? extends AnnotatedElement> actual = processor.getElementsToProcess();

        assertEquals(5, actual.size());
        assertContainsClass(actual, Sample.class.getName());
        assertContainsClass(actual, RelatedSample.class.getName());
        assertContainsClass(actual, AnotherSample.class.getName());
    }

    @Test
    public void shouldProcessClassWithAnnotation() throws Exception {
        processor.process(Sample.class);

        verify(fieldProcessor).setClazz(Sample.class);
        verify(fieldProcessor).setEntity(any(EntityDto.class));
        verify(fieldProcessor).execute(bundle);

        verify(uiFilterableProcessor).setClazz(Sample.class);
        verify(uiFilterableProcessor).execute(bundle);

        verify(uiDisplayableProcessor).setClazz(Sample.class);
        verify(uiDisplayableProcessor).execute(bundle);

        verify(nonEditableProcessor).setClazz(Sample.class);
        verify(nonEditableProcessor).execute(bundle);
    }

    @Test
    public void shouldSetRecordHistoryFlag() {
        processor.process(AnotherSample.class);

        verify(crudEventsProcessor).setClazz(AnotherSample.class);
        verify(crudEventsProcessor).setTrackingDto(trackingDtoCaptor.capture());
        verify(crudEventsProcessor).execute(bundle);

        TrackingDto trackingDto = trackingDtoCaptor.getValue();
        assertFalse(trackingDto.isRecordHistory());

        processor.process(Sample.class);

        verify(crudEventsProcessor).setClazz(Sample.class);
        verify(crudEventsProcessor, times(2)).setTrackingDto(trackingDtoCaptor.capture());
        verify(crudEventsProcessor, times(2)).execute(bundle);

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
    public void shouldFailProcessingClassesWithInvalidSecurityOptions() {
        processor.process(InvalidSecuritySample.class);

        verifyZeroInteractions(fieldProcessor);

        processor.process(AnotherInvalidSecuritySample.class);

        verifyZeroInteractions(fieldProcessor);
    }

    @Test
    public void shouldNotProcessClassWithoutAnnotation() throws Exception {
        processor.process(Object.class);

        verifyZeroInteractions(entityService, fieldProcessor);
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
