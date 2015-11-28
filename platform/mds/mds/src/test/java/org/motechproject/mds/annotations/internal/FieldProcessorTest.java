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
import org.motechproject.mds.annotations.internal.samples.RelatedSample;
import org.motechproject.mds.annotations.internal.samples.Sample;
import org.motechproject.mds.domain.ManyToOneRelationship;
import org.motechproject.mds.domain.OneToManyRelationship;
import org.motechproject.mds.domain.OneToOneRelationship;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.dto.TypeValidationDto;
import org.motechproject.mds.dto.ValidationCriterionDto;
import org.motechproject.mds.util.Constants;

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
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang.reflect.FieldUtils.getDeclaredField;
import static org.apache.commons.lang.reflect.MethodUtils.getAccessibleMethod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.mds.testutil.MemberTestUtil.assertHasField;

@RunWith(MockitoJUnitRunner.class)
public class FieldProcessorTest {

    @Spy
    private MockBundle bundle = new MockBundle();

    @Mock
    private SchemaHolder schemaHolder;

    @Captor
    private ArgumentCaptor<EntityDto> entityCaptor;

    @Captor
    private ArgumentCaptor<FieldDto> fieldCaptor;

    private FieldProcessor processor;

    private EntityDto entity = new EntityDto(1L, Sample.class.getName());

    @Before
    public void setUp() throws Exception {
        processor = new FieldProcessor();
        processor.setEntity(entity);
        processor.setClazz(Sample.class);
        processor.setBundle(bundle);
        processor.setSchemaHolder(schemaHolder);

        doReturn(TypeDto.STRING).when(schemaHolder).getType(String.class);
        doReturn(TypeDto.STRING).when(schemaHolder).getType(String.class.getName());
        doReturn(TypeDto.TIME).when(schemaHolder).getType(Time.class);
        doReturn(TypeDto.DATE).when(schemaHolder).getType(Date.class);
        doReturn(TypeDto.BOOLEAN).when(schemaHolder).getType(Boolean.class);
        doReturn(TypeDto.BOOLEAN).when(schemaHolder).getType(boolean.class);
        doReturn(TypeDto.DOUBLE).when(schemaHolder).getType(Double.class);
        doReturn(TypeDto.DOUBLE).when(schemaHolder).getType(Double.class.getName());
        doReturn(TypeDto.DOUBLE).when(schemaHolder).getType(double.class);
        doReturn(TypeDto.INTEGER).when(schemaHolder).getType(Integer.class.getName());
        doReturn(TypeDto.INTEGER).when(schemaHolder).getType(Integer.class);
        doReturn(TypeDto.INTEGER).when(schemaHolder).getType(int.class);
        doReturn(TypeDto.LONG).when(schemaHolder).getType(Long.class);
        doReturn(TypeDto.LONG).when(schemaHolder).getType(long.class);
        doReturn(TypeDto.COLLECTION).when(schemaHolder).getType(Collection.class);
        doReturn(TypeDto.ONE_TO_ONE_RELATIONSHIP).when(schemaHolder).getType(OneToOneRelationship.class);
        doReturn(TypeDto.ONE_TO_MANY_RELATIONSHIP).when(schemaHolder).getType(OneToManyRelationship.class);
        doReturn(TypeDto.MANY_TO_ONE_RELATIONSHIP).when(schemaHolder).getType(ManyToOneRelationship.class);
        when(schemaHolder.getEntityByClassName(anyString())).thenReturn(null);
    }

    @Test
    public void shouldReturnCorrectAnnotation() throws Exception {
        assertEquals(Field.class, processor.getAnnotationType());
    }

    @Test
    public void shouldReturnCorrectElementList() throws Exception {
        List<AnnotatedElement> actual = new ArrayList<>();
        actual.addAll(processor.getElementsToProcess());

        assertEquals(Sample.FIELD_COUNT, actual.size());
        assertHasField(actual, "world");
        assertHasField(actual, "pi");
        assertHasField(actual, "serverDate");
        assertHasField(actual, "localTime");
    }

    @Test
    public void shouldProcessField() throws Exception {
        java.lang.reflect.Field world = getDeclaredField(Sample.class, "world", true);

        processor.process(world);

        verify(schemaHolder).getType(Boolean.class);

        Collection<FieldDto> fields = processor.getElements();

        assertEquals(1, fields.size());

        FieldDto field = fields.iterator().next();

        assertEquals(entity.getId(), field.getEntityId());
        assertEquals("World", field.getBasic().getDisplayName());
        assertEquals(world.getName(), field.getBasic().getName());
        assertFalse(field.getBasic().isRequired());
        assertEquals("", field.getBasic().getDefaultValue());
        assertEquals("", field.getBasic().getTooltip());
        assertEquals("", field.getBasic().getPlaceholder());

        assertEquals(entity.getId(), field.getEntityId());
        assertEquals(TypeDto.BOOLEAN, field.getType());
    }

    @Test
    public void shouldNotProcessPublicFieldWithIgnoreAnnotation() {
        AnnotatedElement ignored = getDeclaredField(Sample.class, "ignored", true);

        processor.process(ignored);

        Collection<FieldDto> fields = processor.getElements();
        assertEquals(1, fields.size());

        List<AnnotatedElement> actual = new ArrayList<>();
        actual.addAll(processor.getElementsToProcess());

        assertEquals(Sample.FIELD_COUNT, actual.size());
        assertFalse(actual.contains(ignored));
    }

    @Test
    public void shouldProcessSetter() throws Exception {
        Method setLocalTime = getAccessibleMethod(Sample.class, "setLocalTime", Time.class);

        processor.process(setLocalTime);

        verify(schemaHolder).getType(Time.class);

        Collection<FieldDto> fields = processor.getElements();

        assertEquals(1, fields.size());

        FieldDto field = fields.iterator().next();

        assertEquals("Local Time", field.getBasic().getDisplayName());
        assertEquals("localTime", field.getBasic().getName());
        assertTrue(field.getBasic().isRequired());
        assertEquals("", field.getBasic().getDefaultValue());
        assertEquals("", field.getBasic().getTooltip());
        assertEquals("", field.getBasic().getPlaceholder());

        assertEquals(entity.getId(), field.getEntityId());
        assertEquals(TypeDto.TIME, field.getType());
    }

    @Test
    public void shouldProcessGetter() throws Exception {
        Method getServerDate = getAccessibleMethod(Sample.class, "getServerDate", new Class[0]);

        processor.process(getServerDate);

        verify(schemaHolder).getType(Date.class);

        Collection<FieldDto> fields = processor.getElements();

        assertEquals(1, fields.size());

        FieldDto field = fields.iterator().next();

        assertEquals("Server Date", field.getBasic().getDisplayName());
        assertEquals("serverDate", field.getBasic().getName());
        assertFalse(field.getBasic().isRequired());
        assertEquals("", field.getBasic().getDefaultValue());
        assertEquals("", field.getBasic().getTooltip());
        assertEquals("yyyy-mm-dd", field.getBasic().getPlaceholder());

        assertEquals(entity.getId(), field.getEntityId());
        assertEquals(TypeDto.DATE, field.getType());
    }

    @Test
    public void shouldNotProcessIgnoredSettersAndGetters() {
        Method setIgnoredField = getAccessibleMethod(Sample.class, "setIgnoredPrivate", String.class);
        Method getIgnoredField = getAccessibleMethod(Sample.class, "getIgnoredPrivate", new Class[0]);

        processor.process(setIgnoredField);
        processor.process(getIgnoredField);
        verify(schemaHolder, times(2)).getType(String.class);

        Collection<FieldDto> setterFields = processor.getElements();
        assertEquals(1, setterFields.size());

        List<AnnotatedElement> actual = new ArrayList<>();
        actual.addAll(processor.getElementsToProcess());

        assertEquals(Sample.FIELD_COUNT, actual.size());
        assertFalse(actual.contains(getIgnoredField));
        assertFalse(actual.contains(setIgnoredField));
    }

    @Test
    public void shouldAssignFieldValidation() throws Exception {
        TypeValidationDto intMinValue = new TypeValidationDto("mds.field.validation.minValue", Integer.class.getName());
        TypeValidationDto intMaxValue = new TypeValidationDto("mds.field.validation.maxValue", Integer.class.getName());
        TypeValidationDto intMustBeInSet = new TypeValidationDto("mds.field.validation.mustBeInSet", String.class.getName());
        TypeValidationDto intCannotBeInSet = new TypeValidationDto("mds.field.validation.cannotBeInSet", String.class.getName());

        TypeValidationDto decMinValue = new TypeValidationDto("mds.field.validation.minValue", Double.class.getName());
        TypeValidationDto decMaxValue = new TypeValidationDto("mds.field.validation.maxValue", Double.class.getName());
        TypeValidationDto decMustBeInSet = new TypeValidationDto("mds.field.validation.mustBeInSet", String.class.getName());
        TypeValidationDto decCannotBeInSet = new TypeValidationDto("mds.field.validation.cannotBeInSet", String.class.getName());

        TypeValidationDto regex = new TypeValidationDto("mds.field.validation.regex", String.class.getName());
        TypeValidationDto minLength = new TypeValidationDto("mds.field.validation.minLength", Integer.class.getName());
        TypeValidationDto maxLength = new TypeValidationDto("mds.field.validation.maxLength", Integer.class.getName());

        doReturn(singletonList(intMinValue)).when(schemaHolder).findValidations(Integer.class.getName(), DecimalMin.class);
        doReturn(singletonList(intMaxValue)).when(schemaHolder).findValidations(Integer.class.getName(), DecimalMax.class);
        doReturn(singletonList(intMustBeInSet)).when(schemaHolder).findValidations(Integer.class.getName(), InSet.class);
        doReturn(singletonList(intCannotBeInSet)).when(schemaHolder).findValidations(Integer.class.getName(), NotInSet.class);
        doReturn(singletonList(intMinValue)).when(schemaHolder).findValidations(Integer.class.getName(), Min.class);
        doReturn(singletonList(intMaxValue)).when(schemaHolder).findValidations(Integer.class.getName(), Max.class);

        doReturn(singletonList(decMinValue)).when(schemaHolder).findValidations(Double.class.getName(), DecimalMin.class);
        doReturn(singletonList(decMaxValue)).when(schemaHolder).findValidations(Double.class.getName(), DecimalMax.class);
        doReturn(singletonList(decMustBeInSet)).when(schemaHolder).findValidations(Double.class.getName(), InSet.class);
        doReturn(singletonList(decCannotBeInSet)).when(schemaHolder).findValidations(Double.class.getName(), NotInSet.class);
        doReturn(singletonList(decMinValue)).when(schemaHolder).findValidations(Double.class.getName(), Min.class);
        doReturn(singletonList(decMaxValue)).when(schemaHolder).findValidations(Double.class.getName(), Max.class);

        doReturn(singletonList(regex)).when(schemaHolder).findValidations(String.class.getName(), Pattern.class);
        doReturn(asList(minLength, maxLength)).when(schemaHolder).findValidations(String.class.getName(), Size.class);
        doReturn(singletonList(minLength)).when(schemaHolder).findValidations(String.class.getName(), DecimalMin.class);
        doReturn(singletonList(maxLength)).when(schemaHolder).findValidations(String.class.getName(), DecimalMax.class);

        processor.execute(bundle, schemaHolder);
        Collection<FieldDto> fields = processor.getElements();

        FieldDto pi = findFieldWithName(fields, "pi");
        assertCriterion(pi, "mds.field.validation.minValue", "3");
        assertCriterion(pi, "mds.field.validation.maxValue", "4");
        assertCriterion(pi, "mds.field.validation.mustBeInSet", "3,3.14,4");
        assertCriterion(pi, "mds.field.validation.cannotBeInSet", "1,2,5");

        FieldDto epsilon = findFieldWithName(fields, "epsilon");
        assertCriterion(epsilon, "mds.field.validation.minValue", "0.0");
        assertCriterion(epsilon, "mds.field.validation.maxValue", "1.0");
        assertCriterion(epsilon, "mds.field.validation.mustBeInSet", "1,0.75,0.5,0.25,0");
        assertCriterion(epsilon, "mds.field.validation.cannotBeInSet", "-1,2,3");

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

    @Test
    public void shouldReadMaxLengthForStringField() {
        Method getLength400 = getAccessibleMethod(Sample.class, "getLength400", new Class[0]);

        processor.process(getLength400);

        Collection<FieldDto> fields = processor.getElements();
        assertEquals(1, fields.size());
        FieldDto field = fields.iterator().next();

        assertEquals("length400", field.getBasic().getName());
        SettingDto lengthSetting = field.getSetting(Constants.Settings.STRING_MAX_LENGTH);
        assertNotNull(lengthSetting);
        assertEquals(400, lengthSetting.getValue());
    }

    @Test
    public void shouldRecognizeRelationshipTypes() throws NoSuchFieldException {
        processor.process(Sample.class.getDeclaredField("oneToOneUni"));
        processor.process(Sample.class.getDeclaredField("oneToOneBi"));
        processor.process(Sample.class.getDeclaredField("oneToManyUni"));
        processor.process(Sample.class.getDeclaredField("oneToManyBi"));
        processor.process(RelatedSample.class.getDeclaredField("oneToOneBi2"));
        processor.process(RelatedSample.class.getDeclaredField("manyToOneBi"));

        Collection<FieldDto> fields = processor.getElements();
        assertEquals(6, fields.size());

        assertRelationshipField(findFieldWithName(fields, "oneToOneUni"),
                RelatedSample.class, OneToOneRelationship.class, null);
        assertRelationshipField(findFieldWithName(fields, "oneToOneBi"),
                RelatedSample.class, OneToOneRelationship.class, "oneToOneBi2");
        assertRelationshipField(findFieldWithName(fields, "oneToManyUni"),
                RelatedSample.class, OneToManyRelationship.class, null,
                new ExpectedCascadeSettings(true, false, true), Set.class);
        assertRelationshipField(findFieldWithName(fields, "oneToManyBi"),
                RelatedSample.class, OneToManyRelationship.class, "manyToOneBi",
                new ExpectedCascadeSettings(false, false, true), List.class);
        assertRelationshipField(findFieldWithName(fields, "oneToOneBi2"),
                Sample.class, OneToOneRelationship.class, "oneToOneBi");
        assertRelationshipField(findFieldWithName(fields, "manyToOneBi"),
                Sample.class, ManyToOneRelationship.class, "oneToManyBi");
    }

    @Test
    public void shouldProcessComboboxFields() throws NoSuchFieldException {
        processor.process(Sample.class.getDeclaredField("enumSet"));
        processor.process(Sample.class.getDeclaredField("stringSet"));

        Collection<FieldDto> fields = processor.getElements();
        assertEquals(2, fields.size());

        FieldDto field= findFieldWithName(fields, "enumSet");
        assertEquals(2, field.getMetadata().size());
        assertEquals(Set.class.getName(), field.getMetadata(Constants.MetadataKeys.ENUM_COLLECTION_TYPE).getValue());
        assertEquals("org.motechproject.mds.annotations.internal.samples.Sample$TestEnum", field.getMetadata(Constants.MetadataKeys.ENUM_CLASS_NAME).getValue());

        assertEquals(3, field.getSettings().size());
        assertTrue(Boolean.parseBoolean(field.getSettingsValueAsString(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS)));
        assertFalse(Boolean.parseBoolean(field.getSettingsValueAsString(Constants.Settings.ALLOW_USER_SUPPLIED)));
        assertEquals("{ONE=one, TWO=two, THREE=three}", field.getSettingsValueAsString(Constants.Settings.COMBOBOX_VALUES));

        field= findFieldWithName(fields, "stringSet");
        assertEquals(1, field.getMetadata().size());
        assertEquals(Set.class.getName(), field.getMetadata(Constants.MetadataKeys.ENUM_COLLECTION_TYPE).getValue());

        assertEquals(3, field.getSettings().size());
        assertTrue(Boolean.parseBoolean(field.getSettingsValueAsString(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS)));
        assertTrue(Boolean.parseBoolean(field.getSettingsValueAsString(Constants.Settings.ALLOW_USER_SUPPLIED)));
        assertEquals("{}", field.getSettingsValueAsString(Constants.Settings.COMBOBOX_VALUES));
    }

    @Test
    public void shouldProcessComboboxFieldsForSingleEnumInstance() throws NoSuchFieldException {
        processor.process(Sample.class.getDeclaredField("singleEnum"));

        Collection<FieldDto> fields = processor.getElements();
        assertEquals(1, fields.size());

        FieldDto field= findFieldWithName(fields, "singleEnum");
        assertEquals(1, field.getMetadata().size());
        assertEquals("org.motechproject.mds.annotations.internal.samples.Sample$TestEnum", field.getMetadata(Constants.MetadataKeys.ENUM_CLASS_NAME).getValue());

        assertEquals(3, field.getSettings().size());
        assertFalse(Boolean.parseBoolean(field.getSettingsValueAsString(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS)));
        assertFalse(Boolean.parseBoolean(field.getSettingsValueAsString(Constants.Settings.ALLOW_USER_SUPPLIED)));
        assertEquals("{ONE=one, TWO=two, THREE=three}", field.getSettingsValueAsString(Constants.Settings.COMBOBOX_VALUES));
    }

    @Test
    public void shouldProcessPrimitiveFields() throws NoSuchFieldException {
        processor.process(Sample.class.getDeclaredField("primitiveBool"));
        processor.process(Sample.class.getDeclaredField("primitiveInt"));
        processor.process(Sample.class.getDeclaredField("primitiveDouble"));
        processor.process(Sample.class.getDeclaredField("primitiveLong"));

        Collection<FieldDto> fields = processor.getElements();
        assertEquals(4, fields.size());

        assertPrimitiveField(findFieldWithName(fields, "primitiveBool"), Boolean.class, "false");
        assertPrimitiveField(findFieldWithName(fields, "primitiveInt"), Integer.class, "0");
        assertPrimitiveField(findFieldWithName(fields, "primitiveDouble"), Double.class, "0.0");
        assertPrimitiveField(findFieldWithName(fields, "primitiveLong"), Long.class, "0");
    }

    private void assertRelationshipField(FieldDto field, Class<?> relatedClass,
                                         Class<?> relationshipType, String relatedFieldName) {
        assertRelationshipField(field, relatedClass, relationshipType, relatedFieldName, null, null);
    }

    private void assertRelationshipField(FieldDto field, Class<?> relatedClass,
                                       Class<?> relationshipType, String relatedFieldName,
                                       ExpectedCascadeSettings expectedCascadeSettings, Class collectionType) {
        assertEquals(relationshipType.getName(), field.getType().getTypeClass());

        MetadataDto md = field.getMetadata(Constants.MetadataKeys.RELATED_CLASS);
        assertNotNull(md);
        assertEquals(relatedClass.getName(), md.getValue());

        if (relatedFieldName != null) {
            md = field.getMetadata(Constants.MetadataKeys.RELATED_FIELD);
            assertNotNull(md);
            assertEquals(relatedFieldName, md.getValue());
        } else {
            assertNull(field.getMetadata(Constants.MetadataKeys.RELATED_FIELD));
        }

        if (expectedCascadeSettings != null) {
            assertEquals(expectedCascadeSettings.isPersist(), getCascadeSetting(field, Constants.Settings.CASCADE_PERSIST));
            assertEquals(expectedCascadeSettings.isUpdate(), getCascadeSetting(field, Constants.Settings.CASCADE_UPDATE));
            assertEquals(expectedCascadeSettings.isDelete(), getCascadeSetting(field, Constants.Settings.CASCADE_DELETE));
        }

        if (collectionType != null) {
            md = field.getMetadata(Constants.MetadataKeys.RELATIONSHIP_COLLECTION_TYPE);
            assertNotNull(md);
            assertEquals(collectionType.getName(), md.getValue());
        }
    }

    private FieldDto findFieldWithName(Collection<FieldDto> fields, String name) {
        return (FieldDto) CollectionUtils.find(
                fields, new BeanPropertyValueEqualsPredicate("basic.name", name)
        );
    }

    private Boolean getCascadeSetting(FieldDto field, String settingStr) {
        SettingDto setting = field.getSetting(settingStr);
        if (setting == null) {
            return null;
        } else {
            return (Boolean) setting.getValue();
        }
    }

    private void assertCriterion(FieldDto field, String displayName, String value) {
        ValidationCriterionDto dto = field.getValidation().getCriterion(displayName);

        assertNotNull("Criterion " + displayName + " should exists", dto);
        assertEquals(value, String.valueOf(dto.getValue()));
        assertTrue("The validation criterion should be enabled", dto.isEnabled());
    }

    private void assertPrimitiveField(FieldDto field, Class<?> typeClass, String defaultValue) {
        assertNotNull(field);
        assertEquals(typeClass.getName(), field.getType().getTypeClass());
        assertEquals(defaultValue, field.getBasic().getDefaultValue());
        assertTrue(field.getBasic().isRequired());
    }

    private class ExpectedCascadeSettings {

        private final boolean persist;
        private final boolean update;
        private final boolean delete;

        private ExpectedCascadeSettings(boolean persist, boolean update, boolean delete) {
            this.persist = persist;
            this.update = update;
            this.delete = delete;
        }

        public boolean isPersist() {
            return persist;
        }

        public boolean isUpdate() {
            return update;
        }

        public boolean isDelete() {
            return delete;
        }
    }
}
