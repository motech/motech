package org.motechproject.mds.annotations.internal;

import org.eclipse.gemini.blueprint.mock.MockBundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.annotations.UIFilterable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.lang.reflect.FieldUtils.getDeclaredField;
import static org.apache.commons.lang.reflect.MethodUtils.getAccessibleMethod;
import static org.junit.Assert.assertEquals;
import static org.motechproject.mds.testutil.MemberTestUtil.assertHasField;

@RunWith(MockitoJUnitRunner.class)
public class UIFilterableProcessorTest {

    @Spy
    private MockBundle bundle = new MockBundle();

    private UIFilterableProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new UIFilterableProcessor();
        processor.setClazz(Sample.class);
        processor.setBundle(bundle);
    }

    @Test
    public void shouldReturnCorrectAnnotation() throws Exception {
        assertEquals(UIFilterable.class, processor.getAnnotationType());
    }

    @Test
    public void shouldReturnCorrectElementList() throws Exception {
        List<AnnotatedElement> actual = new ArrayList<>();
        actual.addAll(processor.getElementsToProcess());

        assertEquals(Sample.UI_FILTERABLE_FIELD_COUNT, actual.size());
        assertHasField(actual, "world");
        assertHasField(actual, "serverDate");
    }

    @Test
    public void shouldProcessField() throws Exception {
        java.lang.reflect.Field world = getDeclaredField(Sample.class, "world", true);

        processor.process(world);

        Collection<String> fields = processor.getElements();

        assertEquals(1, fields.size());

        String fieldName = fields.iterator().next();

        assertEquals("world", fieldName);
    }

    @Test
    public void shouldProcessGetter() throws Exception {
        Method getServerDate = getAccessibleMethod(Sample.class, "getServerDate", new Class[0]);

        processor.process(getServerDate);

        Collection<String> fields = processor.getElements();

        assertEquals(1, fields.size());

        String fieldName = fields.iterator().next();

        assertEquals("serverDate", fieldName);
    }
}
