package org.motechproject.mds.builder.impl;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import org.apache.commons.lang.reflect.FieldUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.motechproject.mds.annotations.internal.samples.AnotherSample;
import org.motechproject.mds.builder.EntityMetadataBuilder;
import org.motechproject.mds.builder.Sample;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.domain.OneToManyRelationship;
import org.motechproject.mds.domain.OneToOneRelationship;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.javassist.MotechClassPool;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.metadata.ClassMetadata;
import javax.jdo.metadata.ClassPersistenceModifier;
import javax.jdo.metadata.CollectionMetadata;
import javax.jdo.metadata.FieldMetadata;
import javax.jdo.metadata.InheritanceMetadata;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.PackageMetadata;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_CLASS;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_FIELD;
import static org.motechproject.mds.util.Constants.Util.CREATION_DATE_DISPLAY_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.CREATION_DATE_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.CREATOR_DISPLAY_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.CREATOR_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.DATANUCLEUS;
import static org.motechproject.mds.util.Constants.Util.MODIFICATION_DATE_DISPLAY_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFICATION_DATE_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFIED_BY_DISPLAY_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFIED_BY_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.OWNER_DISPLAY_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.OWNER_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.VALUE_GENERATOR;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MotechClassPool.class, FieldUtils.class})
public class EntityMetadataBuilderTest {
    private static final String PACKAGE = "org.motechproject.mds.entity";
    private static final String ENTITY_NAME = "Sample";
    private static final String MODULE = "MrS";
    private static final String NAMESPACE = "arrio";
    private static final String TABLE_NAME = "";

    private static final String CLASS_NAME = String.format("%s.%s", PACKAGE, ENTITY_NAME);
    private static final String TABLE_NAME_1 = String.format("MDS_%s", ENTITY_NAME).toUpperCase();
    private static final String TABLE_NAME_2 = String.format("%s_%s", MODULE, ENTITY_NAME).toUpperCase();
    private static final String TABLE_NAME_3 = String.format("%s_%s_%s", MODULE, NAMESPACE, ENTITY_NAME).toUpperCase();

    private EntityMetadataBuilder entityMetadataBuilder = new EntityMetadataBuilderImpl();

    @Mock
    private Entity entity;

    @Mock
    private Field idField;

    @Mock
    private JDOMetadata jdoMetadata;

    @Mock
    private PackageMetadata packageMetadata;

    @Mock
    private ClassMetadata classMetadata;

    @Mock
    private FieldMetadata idMetadata;

    @Mock
    private InheritanceMetadata inheritanceMetadata;

    @Before
    public void setUp() {
        initMocks(this);

        when(entity.getClassName()).thenReturn(CLASS_NAME);
        when(classMetadata.newFieldMetadata("id")).thenReturn(idMetadata);
        when(classMetadata.newInheritanceMetadata()).thenReturn(inheritanceMetadata);
        when(entity.getField("id")).thenReturn(idField);
        when(entity.isBaseEntity()).thenReturn(true);
    }

    @Test
    public void shouldAddEntityMetadata() throws Exception {
        when(entity.getName()).thenReturn(ENTITY_NAME);
        when(entity.getModule()).thenReturn(MODULE);
        when(entity.getNamespace()).thenReturn(NAMESPACE);
        when(entity.getTableName()).thenReturn(TABLE_NAME);
        when(jdoMetadata.newPackageMetadata(PACKAGE)).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(ENTITY_NAME)).thenReturn(classMetadata);

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class);

        verify(jdoMetadata).newPackageMetadata(PACKAGE);
        verify(packageMetadata).newClassMetadata(ENTITY_NAME);
        verify(classMetadata).setTable(TABLE_NAME_3);
        verifyCommonClassMetadata();
    }

    @Test
    public void shouldAddToAnExistingPackage() {
        when(entity.getName()).thenReturn(ENTITY_NAME);
        when(entity.getTableName()).thenReturn(TABLE_NAME);
        when(jdoMetadata.getPackages()).thenReturn(new PackageMetadata[]{packageMetadata});
        when(packageMetadata.getName()).thenReturn(PACKAGE);
        when(packageMetadata.newClassMetadata(ENTITY_NAME)).thenReturn(classMetadata);

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class);

        verify(jdoMetadata, never()).newPackageMetadata(PACKAGE);
        verify(jdoMetadata).getPackages();
        verify(packageMetadata).newClassMetadata(ENTITY_NAME);
        verify(classMetadata).setTable(TABLE_NAME_1);
        verifyCommonClassMetadata();
    }

    @Test
    public void shouldSetAppropriateTableName() throws Exception {
        when(entity.getName()).thenReturn(ENTITY_NAME);
        when(entity.getTableName()).thenReturn(TABLE_NAME);
        when(jdoMetadata.newPackageMetadata(anyString())).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(anyString())).thenReturn(classMetadata);

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class);
        verify(classMetadata).setTable(TABLE_NAME_1);

        when(entity.getModule()).thenReturn(MODULE);
        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class);
        verify(classMetadata).setTable(TABLE_NAME_2);

        when(entity.getNamespace()).thenReturn(NAMESPACE);
        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class);
        verify(classMetadata).setTable(TABLE_NAME_3);
    }

    @Test
    public void shouldAddBaseEntityMetadata() throws Exception {
        CtField ctField = mock(CtField.class);
        CtClass ctClass = mock(CtClass.class);
        CtClass superClass = mock(CtClass.class);
        ClassData classData = mock(ClassData.class);
        ClassPool pool = mock(ClassPool.class);

        PowerMockito.mockStatic(MotechClassPool.class);
        PowerMockito.when(MotechClassPool.getDefault()).thenReturn(pool);

        when(classData.getClassName()).thenReturn(CLASS_NAME);
        when(classData.getModule()).thenReturn(MODULE);
        when(classData.getNamespace()).thenReturn(NAMESPACE);
        when(entity.getTableName()).thenReturn(TABLE_NAME);
        when(pool.getOrNull(CLASS_NAME)).thenReturn(ctClass);
        when(ctClass.getField("id")).thenReturn(ctField);
        when(ctClass.getSuperclass()).thenReturn(superClass);
        when(superClass.getName()).thenReturn(Object.class.getName());
        when(jdoMetadata.newPackageMetadata(PACKAGE)).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(ENTITY_NAME)).thenReturn(classMetadata);

        entityMetadataBuilder.addBaseMetadata(jdoMetadata, classData, EntityType.STANDARD, Sample.class);

        verify(jdoMetadata).newPackageMetadata(PACKAGE);
        verify(packageMetadata).newClassMetadata(ENTITY_NAME);
        verify(classMetadata).setTable(TABLE_NAME_3);
        verifyCommonClassMetadata();
    }

    @Test
    public void shouldAddOneToManyRelationshipMetadata() {
        Field oneToManyField = mock(Field.class);
        org.motechproject.mds.domain.FieldMetadata entityFmd = mock(org.motechproject.mds.domain.FieldMetadata.class);
        Type oneToManyType = mock(Type.class);

        when(entityFmd.getKey()).thenReturn(RELATED_CLASS);
        when(entityFmd.getValue()).thenReturn("org.motechproject.test.MyClass");

        when(oneToManyType.getTypeClass()).thenReturn((Class) OneToManyRelationship.class);
        when(oneToManyType.isRelationship()).thenReturn(true);

        when(oneToManyField.getName()).thenReturn("oneToManyName");
        when(oneToManyField.getMetadata()).thenReturn(asList(entityFmd));
        when(oneToManyField.getType()).thenReturn(oneToManyType);

        FieldMetadata fmd = mock(FieldMetadata.class);
        CollectionMetadata collMd = mock(CollectionMetadata.class);

        when(entity.getName()).thenReturn(ENTITY_NAME);
        when(entity.getFields()).thenReturn(asList(oneToManyField));
        when(entity.getTableName()).thenReturn(TABLE_NAME);
        when(jdoMetadata.newPackageMetadata(PACKAGE)).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(ENTITY_NAME)).thenReturn(classMetadata);
        when(classMetadata.newFieldMetadata("oneToManyName")).thenReturn(fmd);
        when(fmd.getCollectionMetadata()).thenReturn(collMd);
        when(fmd.getName()).thenReturn("oneToManyName");

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class);

        verifyCommonClassMetadata();
        verify(fmd).setDefaultFetchGroup(true);
        verify(collMd).setEmbeddedElement(false);
        verify(collMd).setSerializedElement(false);
        verify(collMd).setElementType("org.motechproject.test.MyClass");
    }

    @Test
    public void shouldAddOneToOneRelationshipMetadata() throws NotFoundException, CannotCompileException {
        final String myClassName = "org.motechproject.test.MyClass";
        final String myFieldName = "myField";

        Field oneToOneField = mock(Field.class);
        org.motechproject.mds.domain.FieldMetadata relatedClassFmd = mock(org.motechproject.mds.domain.FieldMetadata.class);
        org.motechproject.mds.domain.FieldMetadata relatedFieldFmd = mock(org.motechproject.mds.domain.FieldMetadata.class);

        Type oneToOneType = mock(Type.class);

        when(relatedClassFmd.getKey()).thenReturn(RELATED_CLASS);
        when(relatedClassFmd.getValue()).thenReturn(myClassName);

        when(relatedFieldFmd.getKey()).thenReturn(RELATED_FIELD);
        when(relatedFieldFmd.getValue()).thenReturn(myFieldName);

        when(oneToOneType.getTypeClass()).thenReturn((Class) OneToOneRelationship.class);
        when(oneToOneType.isRelationship()).thenReturn(true);

        when(oneToOneField.getName()).thenReturn("oneToOneName");
        when(oneToOneField.getMetadata(RELATED_FIELD)).thenReturn(relatedFieldFmd);
        when(oneToOneField.getType()).thenReturn(oneToOneType);

        FieldMetadata fmd = mock(FieldMetadata.class);
        when(fmd.getName()).thenReturn("oneToOneName");

        when(entity.getName()).thenReturn(ENTITY_NAME);
        when(entity.getFields()).thenReturn(asList(oneToOneField));
        when(entity.getTableName()).thenReturn(TABLE_NAME);
        when(jdoMetadata.newPackageMetadata(PACKAGE)).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(ENTITY_NAME)).thenReturn(classMetadata);
        when(classMetadata.newFieldMetadata("oneToOneName")).thenReturn(fmd);

        /* We simulate configuration for the bi-directional relationship (the related class has got
           a field that links back to the main class) */
        CtClass myClass = mock(CtClass.class);
        CtClass relatedClass = mock(CtClass.class);
        CtField myField = mock(CtField.class);
        CtField relatedField = mock(CtField.class);

        when(myClass.getName()).thenReturn(myClassName);
        when(myClass.getDeclaredFields()).thenReturn(new CtField[]{myField});

        when(myField.getType()).thenReturn(relatedClass);
        when(myField.getName()).thenReturn(myFieldName);

        when(relatedClass.getDeclaredFields()).thenReturn(new CtField[]{relatedField});
        when(relatedClass.getName()).thenReturn(CLASS_NAME);

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class);

        verifyCommonClassMetadata();
        verify(fmd).setDefaultFetchGroup(true);
        verify(fmd).setPersistenceModifier(PersistenceModifier.PERSISTENT);
    }

    @Test
    public void shouldSetIndexOnMetadataLookupField() throws Exception {
        Field lookupField = mock(Field.class);
        Type string = new Type(String.class);
        Set<org.motechproject.mds.domain.Lookup> lookups = new HashSet<>();
        Lookup lookup = new Lookup();
        lookup.setIndexRequired(true);
        lookups.add(lookup);

        when(lookupField.getName()).thenReturn("lookupField");
        when(lookupField.getType()).thenReturn(string);
        when(lookupField.getLookups()).thenReturn(lookups);

        FieldMetadata fmd = mock(FieldMetadata.class);

        when(entity.getName()).thenReturn(ENTITY_NAME);
        when(entity.getFields()).thenReturn(asList(lookupField));
        when(entity.getTableName()).thenReturn(TABLE_NAME);
        when(jdoMetadata.newPackageMetadata(PACKAGE)).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(ENTITY_NAME)).thenReturn(classMetadata);
        when(classMetadata.newFieldMetadata("lookupField")).thenReturn(fmd);
        PowerMockito.mockStatic(FieldUtils.class);
        when(FieldUtils.getDeclaredField(eq(Sample.class), anyString(), eq(true))).thenReturn(Sample.class.getDeclaredField("notInDefFg"));

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class);

        verifyCommonClassMetadata();
        verify(fmd).setIndexed(true);
    }

    @Test
    public void shouldAddObjectValueGeneratorToAppropriateFields() throws Exception {
        when(entity.getName()).thenReturn(ENTITY_NAME);
        when(entity.getTableName()).thenReturn(TABLE_NAME);
        when(jdoMetadata.newPackageMetadata(anyString())).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(anyString())).thenReturn(classMetadata);

        Type string = new Type(String.class);
        Type dateTime = new Type(DateTime.class);

        List<Field> fields = new ArrayList<>();
        // for these fields the appropriate generator should be added
        fields.add(new Field(entity, CREATOR_FIELD_NAME, CREATOR_DISPLAY_FIELD_NAME, string, true, true));
        fields.add(new Field(entity, OWNER_FIELD_NAME, OWNER_DISPLAY_FIELD_NAME, string, true, true));
        fields.add(new Field(entity, CREATION_DATE_FIELD_NAME, CREATION_DATE_DISPLAY_FIELD_NAME, dateTime, true, true));
        fields.add(new Field(entity, MODIFIED_BY_FIELD_NAME, MODIFIED_BY_DISPLAY_FIELD_NAME, string, true, true));
        fields.add(new Field(entity, MODIFICATION_DATE_FIELD_NAME, MODIFICATION_DATE_DISPLAY_FIELD_NAME, dateTime, true, true));

        doReturn(fields).when(entity).getFields();

        final List<FieldMetadata> list = new ArrayList<>();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                // we create a mock ...
                FieldMetadata metadata = mock(FieldMetadata.class);
                // ... and it should return correct name
                doReturn(invocation.getArguments()[0]).when(metadata).getName();

                // Because we want to check that appropriate methods was executed
                // we added metadata to list and later we will verify conditions
                list.add(metadata);

                // in the end we have to return the mock
                return metadata;
            }
        }).when(classMetadata).newFieldMetadata(anyString());
        PowerMockito.mockStatic(FieldUtils.class);
        when(FieldUtils.getDeclaredField(eq(Sample.class), anyString(), eq(true))).thenReturn(Sample.class.getDeclaredField("notInDefFg"));

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class);

        for (FieldMetadata metadata : list) {
            String name = metadata.getName();
            // the id field should not have set object value generator metadata
            int invocations = "id".equalsIgnoreCase(name) ? 0 : 1;

            verify(classMetadata).newFieldMetadata(name);

            verify(metadata, times(invocations)).setPersistenceModifier(PersistenceModifier.PERSISTENT);
            verify(metadata, times(invocations)).setDefaultFetchGroup(true);
            verify(metadata, times(invocations)).newExtensionMetadata(DATANUCLEUS, VALUE_GENERATOR, "ovg." + name);
        }
    }

    @Test
    public void shouldNotSetDefaultInheritanceStrategyIfUserDefinedOwn() {
        when(entity.getName()).thenReturn(ENTITY_NAME);
        when(entity.getTableName()).thenReturn(TABLE_NAME);
        when(jdoMetadata.newPackageMetadata(anyString())).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(anyString())).thenReturn(classMetadata);

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, AnotherSample.class);

        verifyZeroInteractions(inheritanceMetadata);
    }

    @Test
    public void shouldNotSetDefaultFetchGroupIfSpecified() {
        when(entity.getName()).thenReturn(ENTITY_NAME);
        when(entity.getTableName()).thenReturn(TABLE_NAME);
        when(jdoMetadata.newPackageMetadata(anyString())).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(anyString())).thenReturn(classMetadata);

        Field field = mock(Field.class);
        when(field.getName()).thenReturn("notInDefFg");
        when(field.getType()).thenReturn(new Type(OneToOneRelationship.class));
        when(entity.getFields()).thenReturn(asList(field));

        FieldMetadata fmd = mock(FieldMetadata.class);
        when(fmd.getName()).thenReturn("notInDefFg");
        when(classMetadata.newFieldMetadata("notInDefFg")).thenReturn(fmd);

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class);

        verify(fmd, never()).setDefaultFetchGroup(anyBoolean());
    }

    private void verifyCommonClassMetadata() {
        verify(classMetadata).setDetachable(true);
        verify(classMetadata).setIdentityType(IdentityType.APPLICATION);
        verify(classMetadata).setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);
        verify(idMetadata).setPrimaryKey(true);
        verify(idMetadata).setValueStrategy(IdGeneratorStrategy.INCREMENT);
        verify(inheritanceMetadata).setCustomStrategy("complete-table");
    }
}
