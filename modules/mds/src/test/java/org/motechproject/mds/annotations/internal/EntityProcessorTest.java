package org.motechproject.mds.annotations.internal;

import org.eclipse.gemini.blueprint.mock.MockBundle;
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
import org.osgi.framework.Bundle;

import java.io.File;
import java.lang.reflect.AnnotatedElement;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EntityProcessorTest {

    @Spy
    private MockBundle bundle = new MockBundle();

    @Mock
    private EntityService entityService;

    @Captor
    private ArgumentCaptor<EntityDto> captor;

    private EntityProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new EntityProcessor();
        processor.setEntityService(entityService);
        processor.setBundle(bundle);
    }

    @Test
    public void shouldReturnCorrectAnnotation() throws Exception {
        assertEquals(Entity.class, processor.getAnnotation());
    }

    @Test
    public void shouldReturnCorrectElementList() throws Exception {
        File file = computeTestDataRoot(getClass());
        String location = file.toURI().toURL().toString();

        doReturn(location).when(bundle).getLocation();
        doReturn(Sample.class).when(bundle).loadClass(Sample.class.getName());

        List<AnnotatedElement> expected = new ArrayList<>();
        expected.add(Sample.class);

        List<? extends AnnotatedElement> actual = processor.getElements();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldProcessClassWithAnnotation() throws Exception {
        processor.process(Sample.class);

        verify(entityService).createEntity(captor.capture());

        EntityDto value = captor.getValue();

        assertEquals(Sample.class.getName(), value.getClassName());
        assertEquals(Sample.class.getSimpleName(), value.getName());
        assertEquals(bundle.getSymbolicName(), value.getModule());
        assertNull(value.getNamespace());
    }

    private File computeTestDataRoot(Class anyTestClass) {
        String clsUri = anyTestClass.getName().replace('.', '/') + ".class";
        URL url = anyTestClass.getClassLoader().getResource(clsUri);
        String clsPath = url.getPath();

        return new File(clsPath).getParentFile();
    }
}
