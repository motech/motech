package org.motechproject.mds.annotations.internal;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
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
import org.motechproject.mds.annotations.InSet;
import org.motechproject.mds.annotations.NotInSet;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.domain.TypeValidation;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.dto.ValidationCriterionDto;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.TypeService;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.reflect.FieldUtils.getDeclaredField;
import static org.apache.commons.lang.reflect.MethodUtils.getAccessibleMethod;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
    private EntityService entityService;

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
        processor.setEntityService(entityService);
        processor.setEntity(entity);
        processor.setClazz(Sample.class);
        processor.setBundle(bundle);
    }

    @Test
    public void shouldReturnCorrectAnnotation() throws Exception {
        assertEquals(Field.class, processor.getAnnotationType());
    }

    @Test
    public void shouldReturnCorrectElementList() throws Exception {
        AnnotatedElement world = getDeclaredField(Sample.class, "world", true);
        AnnotatedElement pi = getDeclaredField(Sample.class, "pi", true);
        AnnotatedElement getServerDate = getAccessibleMethod(Sample.class, "getServerDate", new Class[0]);
        AnnotatedElement setLocalTime = getAccessibleMethod(Sample.class, "setLocalTime", Time.class);

        List<AnnotatedElement> actual = new ArrayList<>();
        actual.addAll(processor.getProcessElements());

        assertEquals(Sample.FIELD_COUNT, actual.size());
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

        Collection<FieldDto> fields = processor.getElements();

        assertEquals(1, fields.size());

        FieldDto field = fields.iterator().next();

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

        Collection<FieldDto> fields = processor.getElements();
        assertEquals(1, fields.size());

        List<AnnotatedElement> actual = new ArrayList<>();
        actual.addAll(processor.getProcessElements());

        assertEquals(Sample.FIELD_COUNT, actual.size());
        assertFalse(actual.contains(ignored));
    }

    @Test
    public void shouldProcessSetter() throws Exception {
        Method setLocalTime = getAccessibleMethod(Sample.class, "setLocalTime", Time.class);

        doReturn(TypeDto.TIME).when(typeService).findType(Time.class);

        processor.process(setLocalTime);

        verify(typeService).findType(Time.class);

        Collection<FieldDto> fields = processor.getElements();

        assertEquals(1, fields.size());

        FieldDto field = fields.iterator().next();

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

        Collection<FieldDto> fields = processor.getElements();

        assertEquals(1, fields.size());

        FieldDto field = fields.iterator().next();

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

        Collection<FieldDto> setterFields = processor.getElements();
        assertEquals(1, setterFields.size());

        List<AnnotatedElement> actual = new ArrayList<>();
        actual.addAll(processor.getProcessElements());

        assertEquals(Sample.FIELD_COUNT, actual.size());
        assertFalse(actual.contains(getIgnoredField));
        assertFalse(actual.contains(setIgnoredField));
    }

    @Test
    public void shouldAssignFieldValidation() throws Exception {
        Type integer = new Type(Integer.class);
        Type decimal = new Type(Double.class);
        Type string = new Type(String.class);

        TypeValidation intMinValue = new TypeValidation("mds.field.validation.minValue", integer);
        TypeValidation intMaxValue = new TypeValidation("mds.field.validation.maxValue", integer);
        TypeValidation intMustBeInSet = new TypeValidation("mds.field.validation.mustBeInSet", string);
        TypeValidation intCannotBeInSet = new TypeValidation("mds.field.validation.cannotBeInSet", string);

        TypeValidation decMinValue = new TypeValidation("mds.field.validation.minValue", decimal);
        TypeValidation decMaxValue = new TypeValidation("mds.field.validation.maxValue", decimal);
        TypeValidation decMustBeInSet = new TypeValidation("mds.field.validation.mustBeInSet", string);
        TypeValidation decCannotBeInSet = new TypeValidation("mds.field.validation.cannotBeInSet", string);

        TypeValidation regex = new TypeValidation("mds.field.validation.regex", string);
        TypeValidation minLength = new TypeValidation("mds.field.validation.minLength", integer);
        TypeValidation maxLength = new TypeValidation("mds.field.validation.maxLength", integer);

        doReturn(TypeDto.INTEGER).when(typeService).findType(Integer.class);
        doReturn(TypeDto.DOUBLE).when(typeService).findType(Double.class);
        doReturn(TypeDto.STRING).when(typeService).findType(String.class);

        doReturn(asList(intMinValue)).when(typeService).findValidations(TypeDto.INTEGER, DecimalMin.class);
        doReturn(asList(intMaxValue)).when(typeService).findValidations(TypeDto.INTEGER, DecimalMax.class);
        doReturn(asList(intMustBeInSet)).when(typeService).findValidations(TypeDto.INTEGER, InSet.class);
        doReturn(asList(intCannotBeInSet)).when(typeService).findValidations(TypeDto.INTEGER, NotInSet.class);
        doReturn(asList(intMinValue)).when(typeService).findValidations(TypeDto.INTEGER, Min.class);
        doReturn(asList(intMaxValue)).when(typeService).findValidations(TypeDto.INTEGER, Max.class);

        doReturn(asList(decMinValue)).when(typeService).findValidations(TypeDto.DOUBLE, DecimalMin.class);
        doReturn(asList(decMaxValue)).when(typeService).findValidations(TypeDto.DOUBLE, DecimalMax.class);
        doReturn(asList(decMustBeInSet)).when(typeService).findValidations(TypeDto.DOUBLE, InSet.class);
        doReturn(asList(decCannotBeInSet)).when(typeService).findValidations(TypeDto.DOUBLE, NotInSet.class);
        doReturn(asList(decMinValue)).when(typeService).findValidations(TypeDto.DOUBLE, Min.class);
        doReturn(asList(decMaxValue)).when(typeService).findValidations(TypeDto.DOUBLE, Max.class);

        doReturn(asList(regex)).when(typeService).findValidations(TypeDto.STRING, Pattern.class);
        doReturn(asList(minLength, maxLength)).when(typeService).findValidations(TypeDto.STRING, Size.class);
        doReturn(asList(minLength)).when(typeService).findValidations(TypeDto.STRING, DecimalMin.class);
        doReturn(asList(maxLength)).when(typeService).findValidations(TypeDto.STRING, DecimalMax.class);

        processor.execute();
        Collection<FieldDto> fields = processor.getElements();

        FieldDto pi = findFieldWithName(fields, "pi");
        assertCriterion(pi, "mds.field.validation.minValue", "3");
        assertCriterion(pi, "mds.field.validation.maxValue", "4");
        assertCriterion(pi, "mds.field.validation.mustBeInSet", "{3,3.14,4}");
        assertCriterion(pi, "mds.field.validation.cannotBeInSet", "{1,2,5}");

        FieldDto epsilon = findFieldWithName(fields, "epsilon");
        assertCriterion(epsilon, "mds.field.validation.minValue", "0.0");
        assertCriterion(epsilon, "mds.field.validation.maxValue", "1.0");
        assertCriterion(epsilon, "mds.field.validation.mustBeInSet", "{1,0.75,0.5,0.25,0}");
        assertCriterion(epsilon, "mds.field.validation.cannotBeInSet", "{-1,2,3}");

        FieldDto random = findFieldWithName(fields, "random");
        assertCriterion(random, "mds.field.validation.minValue", "0");
        assertCriterion(random, "mds.field.validation.maxValue", "10");

        FieldDto gaussian = findFieldWithName(fields, "gaussian");
        assertCriterion(gaussian, "mds.field.validation.minValue", "0.0");
        assertCriterion(gaussian, "mds.field.validation.maxValue", "1.0");

        FieldDto poem = findFieldWithName(fields, "poem");
        assertCriterion(poem, "mds.field.validation.regex", "[A-Z][a-z]{9}");
        assertCriterion(poem, "mds.field.validation.minLength", "10");
        assertCriterion(poem, "mds.field.validation.maxLength", "20");

        FieldDto article = findFieldWithName(fields, "article");
        assertCriterion(article, "mds.field.validation.minLength", "100");
        assertCriterion(article, "mds.field.validation.maxLength", "500");
    }

    private FieldDto findFieldWithName(Collection<FieldDto> fields, String name) {
        return (FieldDto) CollectionUtils.find(
                fields, new BeanPropertyValueEqualsPredicate("basic.name", name)
        );
    }

    private void assertCriterion(FieldDto field, String displayName, String value) {
        ValidationCriterionDto dto = field.getValidation().getCriterion(displayName);

        assertNotNull("Criterion " + displayName + " should exists", dto);
        assertEquals(value, String.valueOf(dto.getValue()));
        assertTrue("The validation criterion should be enabled", dto.isEnabled());
    }

}
