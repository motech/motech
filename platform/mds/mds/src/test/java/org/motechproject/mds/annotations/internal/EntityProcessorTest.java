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
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.TypeService;
import org.motechproject.mds.testutil.MockBundle;
import org.osgi.framework.Bundle;

import java.io.File;
import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
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

    private EntityProcessor processor;

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

        List<? extends AnnotatedElement> actual = processor.getElementsToProcess();

        assertEquals(3, actual.size());
        assertTrue(actual.contains(Sample.class));
        assertTrue(actual.contains(RelatedSample.class));
        assertTrue(actual.contains(AnotherSample.class));
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

}
