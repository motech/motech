package org.motechproject.mds.annotations.internal;

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
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.testutil.MockBundle;
import org.osgi.framework.Bundle;

import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
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
    private FieldProcessor fieldProcessor;

    @Mock
    private UIFilterableProcessor uiFilterableProcessor;

    @Mock
    private UIDisplayableProcessor uiDisplayableProcessor;

    @Captor
    private ArgumentCaptor<EntityDto> captor;

    private EntityProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new EntityProcessor();
        processor.setEntityService(entityService);
        processor.setFieldProcessor(fieldProcessor);
        processor.setUIFilterableProcessor(uiFilterableProcessor);
        processor.setUIDisplayableProcessor(uiDisplayableProcessor);
        processor.setBundle(bundle);

        setUpMockBundle();

        when(entityService.createEntity(any(EntityDto.class))).thenReturn(new EntityDto(1L, "SomeEntity"));
    }

    @Test
    public void shouldReturnCorrectAnnotation() throws Exception {
        assertEquals(Entity.class, processor.getAnnotationType());
    }

    @Test
    public void shouldReturnCorrectElementList() throws Exception {
        List<? extends AnnotatedElement> actual = processor.getProcessElements();

        assertEquals(1, actual.size());
        assertEquals(Sample.class.getName(), ((Class<?>) actual.get(0)).getName());
    }

    @Test
    public void shouldProcessClassWithAnnotation() throws Exception {
        doReturn(new EntityDto()).when(entityService).createEntity(any(EntityDto.class));

        processor.process(Sample.class);

        verify(entityService).createEntity(captor.capture());

        verify(fieldProcessor).setClazz(Sample.class);
        verify(fieldProcessor).setEntity(any(EntityDto.class));
        verify(fieldProcessor).execute();

        verify(uiFilterableProcessor).setClazz(Sample.class);
        verify(uiFilterableProcessor).execute();

        verify(uiDisplayableProcessor).setClazz(Sample.class);
        verify(uiDisplayableProcessor).execute();

        EntityDto value = captor.getValue();

        assertEquals(Sample.class.getName(), value.getClassName());
        assertEquals(Sample.class.getSimpleName(), value.getName());
        assertEquals(bundle.getSymbolicName(), value.getModule());
        assertEquals("", value.getNamespace());
    }

    @Test
    public void shouldNotProcessClassWithoutAnnotation() throws Exception {
        processor.process(Object.class);

        verifyZeroInteractions(entityService, fieldProcessor);
    }

    @Override
    protected Map<String, Class> getMappingsForLoader() {
        Map mappings = new LinkedHashMap<>();
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
