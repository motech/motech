package org.motechproject.mds.builder.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.OneToManyRelationship;
import org.motechproject.mds.domain.OneToOneRelationship;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.testutil.EntBuilderTestClass;
import org.motechproject.mds.testutil.RelatedClass;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.MDSClassLoader;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Unique;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.motechproject.mds.testutil.FieldTestHelper.fieldDto;
import static org.motechproject.mds.testutil.FieldTestHelper.fieldDtoWithDefVal;
import static org.motechproject.mds.testutil.FieldTestHelper.newVal;
import static org.motechproject.mds.util.Constants.Util.MODIFICATION_DATE_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFIED_BY_FIELD_NAME;

@RunWith(MockitoJUnitRunner.class)
public class EntityBuilderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityBuilderTest.class);

    private static final String ENTITY_NAME = "xx.yy.BuilderTest";

    private EntityBuilder entityBuilder = new EntityBuilderImpl();

    private MDSClassLoader mdsClassLoader;

    @Mock
    private EntityDto entity;

    private List<FieldDto> fields;

    @Mock
    private Bundle bundle;

    @Before
    public void setUp() {
        mdsClassLoader = MDSClassLoader.getStandaloneInstance(getClass().getClassLoader());
        when(entity.getClassName()).thenReturn(ENTITY_NAME);

        fields = new ArrayList<>();

        FieldDto modificationDateField = fieldDto(MODIFICATION_DATE_FIELD_NAME, DateTime.class);
        FieldDto modifiedByField = fieldDto(MODIFIED_BY_FIELD_NAME, String.class);

        modificationDateField.setReadOnly(true);
        modifiedByField.setReadOnly(true);

        fields.add(modificationDateField);
        fields.add(modifiedByField);
    }

    @Test
    public void shouldBuildAnEntityWithFields() throws Exception {
        FieldDto enumListField = fieldDto("enumList", List.class);
        enumListField.addMetadata(new MetadataDto(Constants.MetadataKeys.ENUM_CLASS_NAME, FieldEnum.class.getName()));
        enumListField.getType().setDisplayName("mds.field.combobox");
        enumListField.addSetting(new SettingDto(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS, "true"));

        fields.addAll(asList(fieldDto("count", Integer.class),
                fieldDto("time", Time.class), fieldDto("str", String.class), fieldDto("dec", Double.class),
                fieldDto("bool", Boolean.class), fieldDto("date", Date.class), fieldDto("dt", DateTime.class),
                fieldDto("ld", LocalDate.class), fieldDto("locale", Locale.class), enumListField,
                fieldDto("CapitalizedName", String.class), fieldDto("jd", java.time.LocalDate.class),
                fieldDto("jdt", LocalDateTime.class), fieldDto("blob", Byte[].class.getName())));

        Class<?> clazz = buildClass();

        assertNotNull(clazz);
        assertField(clazz, "count", Integer.class);
        assertField(clazz, "time", Time.class);
        assertField(clazz, "str", String.class);
        assertField(clazz, "dec", Double.class);
        assertField(clazz, "bool", Boolean.class);
        assertField(clazz, "date", Date.class);
        assertField(clazz, "dt", DateTime.class);
        assertField(clazz, "ld", LocalDate.class);
        assertField(clazz, "locale", Locale.class);
        assertField(clazz, "jd", java.time.LocalDate.class);
        assertField(clazz, "jdt", LocalDateTime.class);
        assertField(clazz, "blob", Byte[].class);
        // should use uncapitalized version
        assertField(clazz, "capitalizedName", String.class);
    }

    @Test
    public void shouldBuildAnEntityWithFieldsWithDefaultValues() throws Exception {
        final Date date = new Date();
        final DateTime dateTime = DateUtil.now();
        final LocalDate localDate = DateUtil.now().plusYears(1).toLocalDate();
        final LocalDateTime javaLocalDateTime = LocalDateTime.now().plusDays(1);
        final java.time.LocalDate javaLocalDate = java.time.LocalDate.now().plusDays(1);

        fields.addAll(asList(fieldDtoWithDefVal("count", Integer.class, 1),
                fieldDtoWithDefVal("time", Time.class, new Time(10, 10)),
                fieldDtoWithDefVal("str", String.class, "defStr"),
                fieldDtoWithDefVal("dec", Double.class, 3.1),
                fieldDtoWithDefVal("bool", Boolean.class, true),
                fieldDtoWithDefVal("date", Date.class, date),
                fieldDtoWithDefVal("dt", DateTime.class, dateTime),
                fieldDtoWithDefVal("ld", LocalDate.class, localDate),
                fieldDtoWithDefVal("locale", Locale.class, Locale.CANADA_FRENCH),
                fieldDtoWithDefVal("jd", java.time.LocalDate.class, javaLocalDate),
                fieldDtoWithDefVal("jdt", LocalDateTime.class, javaLocalDateTime)));

        Class<?> clazz = buildClass();

        assertNotNull(clazz);
        assertField(clazz, "count", Integer.class, 1);
        assertField(clazz, "time", Time.class, new Time(10, 10));
        assertField(clazz, "str", String.class, "defStr");
        assertField(clazz, "dec", Double.class, 3.1);
        assertField(clazz, "bool", Boolean.class, true);
        assertField(clazz, "date", Date.class, date);
        assertField(clazz, "dt", DateTime.class, dateTime);
        assertField(clazz, "ld", LocalDate.class, localDate);
        assertField(clazz, "locale", Locale.class, Locale.CANADA_FRENCH);
        assertField(clazz, "jd", java.time.LocalDate.class, javaLocalDate);
        assertField(clazz, "jdt", LocalDateTime.class, javaLocalDateTime);
    }

    @Test
    public void shouldEditClasses() throws Exception {
        FieldDto firstField = fieldDto("name", Integer.class);
        fields.add(firstField);

        Class<?> clazz = buildClass();
        assertField(clazz, "name", Integer.class);

        fields.remove(firstField);
        fields.add(fieldDto("name2", String.class));

        // reload the classloader for class edit
        mdsClassLoader = MDSClassLoader.getStandaloneInstance(getClass().getClassLoader());

        clazz = buildClass();
        assertField(clazz, "name2", String.class);

        // assert that the first field no longer exists
        try {
            clazz.getDeclaredField("name");
            fail("Field 'name' was preserved in the class, although it was removed from the entity");
        } catch (NoSuchFieldException e) {
            // expected
        }
    }

    @Test
    public void shouldBuildHistoryClass() throws Exception {
        fields.addAll(asList(fieldDto("id", Long.class),
                fieldDto("count", Integer.class), fieldDto("time", Time.class),
                fieldDto("str", String.class), fieldDto("dec", Double.class),
                fieldDto("bool", Boolean.class), fieldDto("date", Date.class),
                fieldDto("dt", DateTime.class), fieldDto("list", List.class),
                fieldDto("jd", java.time.LocalDate.class), fieldDto("jld", LocalDateTime.class)));

        ClassData classData = entityBuilder.buildHistory(entity, fields);
        assertEquals("xx.yy.history.BuilderTest__History", classData.getClassName());

        Class<?> clazz = mdsClassLoader.safeDefineClass(classData.getClassName(), classData.getBytecode());

        assertNotNull(clazz);
        assertField(clazz, StringUtils.uncapitalize(clazz.getSimpleName()) + "CurrentVersion", Long.class);
        assertField(clazz, "id", Long.class);
        assertField(clazz, "count", Integer.class);
        assertField(clazz, "time", Time.class);
        assertField(clazz, "str", String.class);
        assertField(clazz, "dec", Double.class);
        assertField(clazz, "bool", Boolean.class);
        assertField(clazz, "date", Date.class);
        assertField(clazz, "dt", DateTime.class);
        assertField(clazz, "jd", java.time.LocalDate.class);
        assertField(clazz, "jld", LocalDateTime.class);
        assertField(clazz, "list", List.class);
    }

    @Test(expected = NoSuchFieldException.class)
    public void shouldNotAddVersionFieldToTheHistoryClass() throws Exception {
        FieldDto versionField = fieldDto("version", Long.class);
        versionField.addMetadata(new MetadataDto(Constants.MetadataKeys.VERSION_FIELD, "true"));
        fields.addAll(asList(
                fieldDto("id", Long.class), versionField,
                fieldDto("count", Integer.class), fieldDto("str", String.class)
        ));

        ClassData classData = entityBuilder.buildHistory(entity, fields);
        assertEquals("xx.yy.history.BuilderTest__History", classData.getClassName());

        Class<?> clazz = mdsClassLoader.safeDefineClass(classData.getClassName(), classData.getBytecode());

        assertNotNull(clazz);

        try {
            assertField(clazz, "id", Long.class);
            assertField(clazz, "count", Integer.class);
            assertField(clazz, "str", String.class);
        } catch (NoSuchFieldException e) {
            LOGGER.error("Cannot find field in the history class", e);
            fail();
        }
        assertField(clazz, "version", Long.class);
    }

    @Test
    public void shouldBuildEnhancedDDE() throws Exception {
        FieldDto strField = fieldDto("testStr", String.class);
        FieldDto boolField = fieldDto("testBool", Boolean.class);
        strField.setReadOnly(true);
        boolField.setReadOnly(true);

        fields.addAll(asList(
                strField, boolField, fieldDto("fromUser", DateTime.class)
        ));
        when(entity.getClassName()).thenReturn(EntBuilderTestClass.class.getName());

        ClassData classData = entityBuilder.buildDDE(entity, fields, bundle);
        Class<?> builtClass = MDSClassLoader.getStandaloneInstance()
                .defineClass(classData.getClassName(), classData.getBytecode());

        assertField(builtClass, "testStr", String.class, "defValForTestStr");
        assertField(builtClass, "testBool", boolean.class, false, "is");
        assertField(builtClass, "fromUser", DateTime.class);

        // check annotations
        assertNotNull(builtClass.getAnnotation(PersistenceCapable.class));
        java.lang.reflect.Field field = builtClass.getDeclaredField("testStr");
        assertNotNull(field.getAnnotation(Unique.class));
    }

    @Test
    public void shouldBuildRelationshipFields() throws Exception {
        FieldDto oneToOneField = fieldDto("oto", OneToOneRelationship.class);
        oneToOneField.setReadOnly(true);
        oneToOneField.addMetadata(new MetadataDto(Constants.MetadataKeys.RELATED_CLASS, RelatedClass.class.getName()));
        FieldDto oneToManyField = fieldDto("otm", OneToManyRelationship.class);
        oneToManyField.setReadOnly(true);
        oneToManyField.addMetadata(new MetadataDto(Constants.MetadataKeys.RELATED_CLASS, RelatedClass.class.getName()));

        fields.addAll(asList(oneToOneField, oneToManyField));
        when(entity.getClassName()).thenReturn(EntBuilderTestClass.class.getName());

        ClassData classData = entityBuilder.buildDDE(entity, fields, bundle);
        Class<?> builtClass = MDSClassLoader.getStandaloneInstance()
                .defineClass(classData.getClassName(), classData.getBytecode());

        assertField(builtClass, "oto", RelatedClass.class);
        assertField(builtClass, "otm", List.class);
        assertGenericType(builtClass, "otm", List.class, RelatedClass.class);
    }

    @Test
    public void shouldBuildEnumListFieldProperly() throws Exception {
        FieldDto enumListField = fieldDto("enumList", Collection.class);
        enumListField.addMetadata(new MetadataDto(Constants.MetadataKeys.ENUM_CLASS_NAME, FieldEnum.class.getName()));
        enumListField.addSetting(new SettingDto(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS, "true"));

        fields.add(enumListField);

        Class<?> clazz = buildClass();

        assertNotNull(clazz);
        assertField(clazz, "enumList", List.class);

        // verify that getters and setters have correct generic signatures

        Method setter = clazz.getDeclaredMethod("setEnumList", List.class);
        assertNotNull(setter);
        ParameterizedType pType = (ParameterizedType) setter.getGenericParameterTypes()[0];
        assertEquals(FieldEnum.class, pType.getActualTypeArguments()[0]);

        Method getter = clazz.getDeclaredMethod("getEnumList");
        assertNotNull(getter);
        pType = (ParameterizedType) getter.getGenericReturnType();
        assertEquals(FieldEnum.class, pType.getActualTypeArguments()[0]);
    }

    private Class<?> buildClass() {
        ClassData classData = entityBuilder.build(entity, fields);

        assertEquals(ENTITY_NAME, classData.getClassName());

        return mdsClassLoader.safeDefineClass(classData.getClassName(), classData.getBytecode());
    }

    private void assertField(Class<?> clazz, String name, Class<?> fieldType) throws Exception {
        assertField(clazz, name, fieldType, null);
    }

    private void assertField(Class<?> clazz, String name, Class<?> fieldType, Object expectedDefaultVal) throws Exception {
        assertField(clazz, name, fieldType, expectedDefaultVal, "get");
    }

    private void assertField(Class<?> clazz, String name, Class<?> fieldType, Object expectedDefaultVal, String getterPrefix)
            throws Exception {
        java.lang.reflect.Field field = clazz.getDeclaredField(name);

        // make sure this does not fail
        field.getGenericType();

        assertNotNull(field);
        assertEquals(Modifier.PRIVATE, field.getModifiers());
        assertEquals(fieldType, field.getType());

        Object instance = clazz.newInstance();
        Object val = ReflectionTestUtils.getField(instance, name);
        assertEquals(expectedDefaultVal, val);

        // assert getters and setters

        Method getter = clazz.getMethod(getterPrefix + StringUtils.capitalize(name));
        assertEquals(fieldType, getter.getReturnType());
        assertEquals(Modifier.PUBLIC, getter.getModifiers());

        Method setter = clazz.getMethod("set" + StringUtils.capitalize(name), fieldType);
        assertEquals(Void.TYPE, setter.getReturnType());
        assertEquals(Modifier.PUBLIC, setter.getModifiers());

        // getter returns default value
        assertEquals(expectedDefaultVal, getter.invoke(instance));

        // set then get
        Object newVal = newVal(fieldType);
        setter.invoke(instance, newVal);

        assertEquals(newVal, getter.invoke(instance));
    }

    public void assertGenericType(Class<?> clazz, String fieldName,
                                  Class<?> fieldClass, Class<?> genericTypeClass) throws Exception {
        java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
        assertNotNull(field);

        assertEquals(fieldClass, field.getType());
        assertEquals(genericTypeClass, ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
    }

    public static enum FieldEnum {
        FIRST_VAL, SECOND_VAL
    }
}
