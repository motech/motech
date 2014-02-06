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
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.TypeDto;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FieldProcessorTest {

    @Spy
    private MockBundle bundle = new MockBundle();

    @Mock
    private TypeService typeService;

    @Captor
    private ArgumentCaptor<EntityDto> entityCaptor;

    @Captor
    private ArgumentCaptor<FieldDto> fieldCaptor;

    private FieldProcessor processor;

    private EntityDto entity = new EntityDto(1L, Sample.class.getName());

    @Before
    public void setUp() throws Exception {
        processor = new FieldProcessor();
        processor.setTypeService(typeService);
        processor.setEntity(entity);
        processor.setClazz(Sample.class);
        processor.setBundle(bundle);
    }

    @Test
    public void shouldReturnCorrectAnnotation() throws Exception {
        assertEquals(Field.class, processor.getAnnotation());
    }

    @Test
    public void shouldReturnCorrectElementList() throws Exception {
        AnnotatedElement world = getDeclaredField(Sample.class, "world", true);
        AnnotatedElement pi = getDeclaredField(Sample.class, "pi", true);
        AnnotatedElement getServerDate = getAccessibleMethod(Sample.class, "getServerDate", new Class[0]);
        AnnotatedElement setLocalTime = getAccessibleMethod(Sample.class, "setLocalTime", Time.class);

        List<AnnotatedElement> actual = new ArrayList<>();
        actual.addAll(processor.getElements());

        assertEquals(4, actual.size());
        assertThat(actual, hasItem(equalTo(world)));
        assertThat(actual, hasItem(equalTo(pi)));
        assertThat(actual, hasItem(equalTo(getServerDate)));
        assertThat(actual, hasItem(equalTo(setLocalTime)));
    }

    @Test
    public void shouldProcessField() throws Exception {
        java.lang.reflect.Field world = getDeclaredField(Sample.class, "world", true);

        doReturn(TypeDto.BOOLEAN).when(typeService).findType(Boolean.class);

        processor.process(world);

        verify(typeService).findType(Boolean.class);

        List<FieldDto> fields = processor.getFields();

        assertEquals(1, fields.size());

        FieldDto field = fields.get(0);

        assertEquals(entity.getId(), field.getEntityId());
        assertEquals(world.getName(), field.getBasic().getDisplayName());
        assertEquals(world.getName(), field.getBasic().getName());
        assertFalse(field.getBasic().isRequired());
        assertEquals("", field.getBasic().getDefaultValue());
        assertEquals("", field.getBasic().getTooltip());

        assertEquals(entity.getId(), field.getEntityId());
        assertEquals(TypeDto.BOOLEAN, field.getType());
    }

    @Test
    public void shouldNotProcessPublicFieldWithIgnoreAnnotation() {
        AnnotatedElement ignored = getDeclaredField(Sample.class, "ignored", true);
        doReturn(TypeDto.STRING).when(typeService).findType(String.class);

        processor.process(ignored);

        List<FieldDto> fields = processor.getFields();
        assertEquals(1, fields.size());

        List<AnnotatedElement> actual = new ArrayList<>();
        actual.addAll(processor.getElements());

        assertEquals(4, actual.size());
        assertFalse(actual.contains(ignored));
    }

    @Test
    public void shouldProcessSetter() throws Exception {
        Method setLocalTime = getAccessibleMethod(Sample.class, "setLocalTime", Time.class);

        doReturn(TypeDto.TIME).when(typeService).findType(Time.class);

        processor.process(setLocalTime);

        verify(typeService).findType(Time.class);

        List<FieldDto> fields = processor.getFields();

        assertEquals(1, fields.size());

        FieldDto field = fields.get(0);

        assertEquals("localTime", field.getBasic().getDisplayName());
        assertEquals("localTime", field.getBasic().getName());
        assertTrue(field.getBasic().isRequired());
        assertEquals("", field.getBasic().getDefaultValue());
        assertEquals("", field.getBasic().getTooltip());

        assertEquals(entity.getId(), field.getEntityId());
        assertEquals(TypeDto.TIME, field.getType());
    }

    @Test
    public void shouldProcessGetter() throws Exception {
        Method getServerDate = getAccessibleMethod(Sample.class, "getServerDate", new Class[0]);

        doReturn(TypeDto.DATE).when(typeService).findType(Date.class);

        processor.process(getServerDate);

        verify(typeService).findType(Date.class);

        List<FieldDto> fields = processor.getFields();

        assertEquals(1, fields.size());

        FieldDto field = fields.get(0);

        assertEquals("Server Date", field.getBasic().getDisplayName());
        assertEquals("serverDate", field.getBasic().getName());
        assertFalse(field.getBasic().isRequired());
        assertEquals("", field.getBasic().getDefaultValue());
        assertEquals("", field.getBasic().getTooltip());

        assertEquals(entity.getId(), field.getEntityId());
        assertEquals(TypeDto.DATE, field.getType());
    }

    @Test
    public void shouldNotProcessIgnoredSettersAndGetters() {
        Method setIgnoredField = getAccessibleMethod(Sample.class, "setIgnoredPrivate", String.class);
        Method getIgnoredField = getAccessibleMethod(Sample.class, "getIgnoredPrivate", new Class[0]);

        doReturn(TypeDto.STRING).when(typeService).findType(String.class);

        processor.process(setIgnoredField);
        processor.process(getIgnoredField);
        verify(typeService, times(2)).findType(String.class);

        List<FieldDto> setterFields = processor.getFields();
        assertEquals(2, setterFields.size());

        List<AnnotatedElement> actual = new ArrayList<>();
        actual.addAll(processor.getElements());

        assertEquals(4, actual.size());
        assertFalse(actual.contains(getIgnoredField));
        assertFalse(actual.contains(setIgnoredField));
    }

}
