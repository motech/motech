package org.motechproject.mds.annotations.internal;

import org.eclipse.gemini.blueprint.mock.MockBundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.annotations.UIFilterable;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.TypeService;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang.reflect.FieldUtils.getDeclaredField;
import static org.apache.commons.lang.reflect.MethodUtils.getAccessibleMethod;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class UIFilterableProcessorTest {

    @Spy
    private MockBundle bundle = new MockBundle();

    @Mock
    private EntityService entityService;

    @Mock
    private TypeService typeService;

    private UIFilterableProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new UIFilterableProcessor();
        processor.setTypeService(typeService);
        processor.setClazz(Sample.class);
        processor.setBundle(bundle);
    }

    @Test
    public void shouldReturnCorrectAnnotation() throws Exception {
        assertEquals(UIFilterable.class, processor.getAnnotation());
    }

    @Test
    public void shouldReturnCorrectElementList() throws Exception {
        AnnotatedElement world = getDeclaredField(Sample.class, "world", true);
        AnnotatedElement getServerDate = getAccessibleMethod(Sample.class, "getServerDate", new Class[0]);

        List<AnnotatedElement> actual = new ArrayList<>();
        actual.addAll(processor.getElements());

        assertEquals(4, actual.size());
        assertThat(actual, hasItem(equalTo(world)));
        assertThat(actual, hasItem(equalTo(getServerDate)));
    }

    @Test
    public void shouldProcessField() throws Exception {
        java.lang.reflect.Field world = getDeclaredField(Sample.class, "world", true);

        doReturn(TypeDto.BOOLEAN).when(typeService).findType(Boolean.class);

        processor.process(world);

        verify(typeService).findType(Boolean.class);

        List<String> fields = processor.getFields();

        assertEquals(1, fields.size());

        String fieldName = fields.get(0);

        assertEquals("world", fieldName);
    }

    @Test
    public void shouldProcessGetter() throws Exception {
        Method getServerDate = getAccessibleMethod(Sample.class, "getServerDate", new Class[0]);

        doReturn(TypeDto.DATE).when(typeService).findType(Date.class);

        processor.process(getServerDate);

        verify(typeService).findType(Date.class);

        List<String> fields = processor.getFields();

        assertEquals(1, fields.size());

        String fieldName = fields.get(0);

        assertEquals("serverDate", fieldName);
    }

    @Test
    public void shouldNotProcessElementWithIncorrectType() throws Exception {
        java.lang.reflect.Field pi = getDeclaredField(Sample.class, "pi", true);

        doReturn(TypeDto.INTEGER).when(typeService).findType(Integer.class);

        processor.process(pi);

        verify(typeService).findType(Integer.class);
        verifyZeroInteractions(entityService);
    }

}
