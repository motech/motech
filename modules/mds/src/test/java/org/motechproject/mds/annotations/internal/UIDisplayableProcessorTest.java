package org.motechproject.mds.annotations.internal;

import org.eclipse.gemini.blueprint.mock.MockBundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.annotations.UIDisplayable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.reflect.FieldUtils.getDeclaredField;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class UIDisplayableProcessorTest {
    private static final String FIELD_NAME = "money";

    @Spy
    private MockBundle bundle = new MockBundle();

    private UIDisplayableProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new UIDisplayableProcessor();
        processor.setClazz(Sample.class);
        processor.setBundle(bundle);
    }

    @Test
    public void shouldReturnCorrectAnnotation() throws Exception {
        assertEquals(UIDisplayable.class, processor.getAnnotation());
    }

    @Test
    public void shouldReturnCorrectElementList() throws Exception {
        AnnotatedElement money = getDeclaredField(Sample.class, FIELD_NAME, true);

        List<AnnotatedElement> actual = new ArrayList<>();
        actual.addAll(processor.getElements());

        assertEquals(Sample.FIELD_COUNT, actual.size());
        assertThat(actual, hasItem(equalTo(money)));
    }

    @Test
    public void shouldProcessField() throws Exception {
        Field world = getDeclaredField(Sample.class, FIELD_NAME, true);

        processor.process(world);

        Map<String, Long> positions = processor.getPositions();

        assertEquals(1, positions.size());
        assertTrue(positions.containsValue(0L));
        assertTrue(positions.containsKey(FIELD_NAME));
    }

}
