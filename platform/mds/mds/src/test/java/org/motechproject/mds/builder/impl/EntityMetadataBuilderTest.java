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
import org.motechproject.mds.builder.SampleWithIncrementStrategy;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.domain.OneToManyRelationship;
import org.motechproject.mds.domain.OneToOneRelationship;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.LookupFieldType;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.dto.TypeDto;
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
import javax.jdo.metadata.ForeignKeyMetadata;
import javax.jdo.metadata.IndexMetadata;
import javax.jdo.metadata.InheritanceMetadata;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.PackageMetadata;
import javax.jdo.metadata.UniqueMetadata;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
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
import static org.motechproject.mds.testutil.FieldTestHelper.fieldDto;
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
    private EntityDto entity;

    @Mock
    private FieldDto idField;

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

    @Mock
    private IndexMetadata indexMetadata;

    @Mock
    private SchemaHolder schemaHolder;

    @Before
    public void setUp() {
        initMocks(this);

        when(entity.getClassName()).thenReturn(CLASS_NAME);
        when(classMetadata.newFieldMetadata("id")).thenReturn(idMetadata);
        when(idMetadata.getName()).thenReturn("id");
        when(classMetadata.newInheritanceMetadata()).thenReturn(inheritanceMetadata);
        when(schemaHolder.getFieldByName(entity, "id")).thenReturn(idField);
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

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class, schemaHolder);

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

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class, schemaHolder);

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

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class, schemaHolder);
        verify(classMetadata).setTable(TABLE_NAME_1);

        when(entity.getModule()).thenReturn(MODULE);
        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class, schemaHolder);
        verify(classMetadata).setTable(TABLE_NAME_2);

        when(entity.getNamespace()).thenReturn(NAMESPACE);
        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class, schemaHolder);
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
        FieldDto oneToManyField = fieldDto("oneToManyName", OneToManyRelationship.class);
        oneToManyField.addMetadata(new MetadataDto(RELATED_CLASS, "org.motechproject.test.MyClass"));

        FieldMetadata fmd = mock(FieldMetadata.class);
        CollectionMetadata collMd = mock(CollectionMetadata.class);

        when(entity.getName()).thenReturn(ENTITY_NAME);
        when(entity.getId()).thenReturn(2L);
        when(schemaHolder.getFields(entity)).thenReturn(singletonList(oneToManyField));
        when(entity.getTableName()).thenReturn(TABLE_NAME);
        when(jdoMetadata.newPackageMetadata(PACKAGE)).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(ENTITY_NAME)).thenReturn(classMetadata);
        when(classMetadata.newFieldMetadata("oneToManyName")).thenReturn(fmd);
        when(fmd.getCollectionMetadata()).thenReturn(collMd);
        when(fmd.getName()).thenReturn("oneToManyName");

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class, schemaHolder);

        verifyCommonClassMetadata();
        verify(fmd).setDefaultFetchGroup(true);
        verify(collMd).setEmbeddedElement(false);
        verify(collMd).setSerializedElement(false);
        verify(collMd).setElementType("org.motechproject.test.MyClass");
    }

    @Test
    public void shouldAddOneToOneRelationshipMetadata() throws NotFoundException, CannotCompileException {
        final String relClassName = "org.motechproject.test.MyClass";
        final String relFieldName = "myField";

        FieldDto oneToOneField = fieldDto("oneToOneName", OneToOneRelationship.class);
        oneToOneField.addMetadata(new MetadataDto(RELATED_CLASS, relClassName));
        oneToOneField.addMetadata(new MetadataDto(RELATED_FIELD, relFieldName));

        FieldMetadata fmd = mock(FieldMetadata.class);
        when(fmd.getName()).thenReturn("oneToOneName");

        ForeignKeyMetadata fkmd = mock(ForeignKeyMetadata.class);

        when(entity.getName()).thenReturn(ENTITY_NAME);
        when(entity.getId()).thenReturn(3L);
        when(schemaHolder.getFields(entity)).thenReturn(singletonList(oneToOneField));
        when(entity.getTableName()).thenReturn(TABLE_NAME);
        when(jdoMetadata.newPackageMetadata(PACKAGE)).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(ENTITY_NAME)).thenReturn(classMetadata);
        when(classMetadata.newFieldMetadata("oneToOneName")).thenReturn(fmd);
        when(fmd.newForeignKeyMetadata()).thenReturn(fkmd);

        /* We simulate configuration for the bi-directional relationship (the related class has got
           a field that links back to the main class) */
        CtClass myClass = mock(CtClass.class);
        CtClass relatedClass = mock(CtClass.class);
        CtField myField = mock(CtField.class);
        CtField relatedField = mock(CtField.class);

        when(myClass.getName()).thenReturn(relClassName);
        when(myClass.getDeclaredFields()).thenReturn(new CtField[]{myField});

        when(myField.getType()).thenReturn(relatedClass);
        when(myField.getName()).thenReturn(relFieldName);

        when(relatedClass.getDeclaredFields()).thenReturn(new CtField[]{relatedField});
        when(relatedClass.getName()).thenReturn(CLASS_NAME);

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class, schemaHolder);

        verifyCommonClassMetadata();
        verify(fmd).setDefaultFetchGroup(true);
        verify(fmd).setPersistenceModifier(PersistenceModifier.PERSISTENT);
        verify(fkmd).setName("fk_Sample_oneToOneName_3");
    }

    @Test
    public void shouldSetIndexOnMetadataLookupField() throws Exception {

        FieldDto lookupField = fieldDto("lookupField", String.class);
        LookupDto lookup = new LookupDto();
        lookup.setLookupName("A lookup");
        lookup.setLookupFields(singletonList(new LookupFieldDto("lookupField", LookupFieldType.VALUE)));
        lookup.setIndexRequired(true);
        lookupField.setLookups(singletonList(lookup));

        FieldMetadata fmd = mock(FieldMetadata.class);

        when(entity.getName()).thenReturn(ENTITY_NAME);
        when(entity.getId()).thenReturn(14L);
        when(schemaHolder.getFields(entity)).thenReturn(singletonList(lookupField));
        when(entity.getTableName()).thenReturn(TABLE_NAME);
        when(jdoMetadata.newPackageMetadata(PACKAGE)).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(ENTITY_NAME)).thenReturn(classMetadata);
        when(classMetadata.newFieldMetadata("lookupField")).thenReturn(fmd);
        when(fmd.newIndexMetadata()).thenReturn(indexMetadata);

        PowerMockito.mockStatic(FieldUtils.class);
        when(FieldUtils.getDeclaredField(eq(Sample.class), anyString(), eq(true))).thenReturn(Sample.class.getDeclaredField("notInDefFg"));

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class, schemaHolder);

        verifyCommonClassMetadata();
        verify(fmd).newIndexMetadata();
        verify(indexMetadata).setName("lkp_idx_" + ENTITY_NAME + "_lookupField_14");
    }

    @Test
    public void shouldAddObjectValueGeneratorToAppropriateFields() throws Exception {
        when(entity.getName()).thenReturn(ENTITY_NAME);
        when(entity.getTableName()).thenReturn(TABLE_NAME);
        when(jdoMetadata.newPackageMetadata(anyString())).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(anyString())).thenReturn(classMetadata);

        List<FieldDto> fields = new ArrayList<>();
        // for these fields the appropriate generator should be added
        fields.add(fieldDto(1L, CREATOR_FIELD_NAME, String.class.getName(), CREATOR_DISPLAY_FIELD_NAME, null));
        fields.add(fieldDto(2L, OWNER_FIELD_NAME, String.class.getName(), OWNER_DISPLAY_FIELD_NAME, null));
        fields.add(fieldDto(3L, CREATION_DATE_FIELD_NAME, DateTime.class.getName(), CREATION_DATE_DISPLAY_FIELD_NAME, null));
        fields.add(fieldDto(4L, MODIFIED_BY_FIELD_NAME, String.class.getName(), MODIFIED_BY_DISPLAY_FIELD_NAME, null));
        fields.add(fieldDto(5L, MODIFICATION_DATE_FIELD_NAME, DateTime.class.getName(), MODIFICATION_DATE_DISPLAY_FIELD_NAME, null));

        doReturn(fields).when(schemaHolder).getFields(CLASS_NAME);

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

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class, schemaHolder);

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

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, AnotherSample.class, schemaHolder);

        verifyZeroInteractions(inheritanceMetadata);
    }

    @Test
    public void shouldNotSetDefaultFetchGroupIfSpecified() {
        when(entity.getName()).thenReturn(ENTITY_NAME);
        when(entity.getTableName()).thenReturn(TABLE_NAME);
        when(jdoMetadata.newPackageMetadata(anyString())).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(anyString())).thenReturn(classMetadata);

        FieldDto field = fieldDto("notInDefFg", OneToOneRelationship.class);
        when(schemaHolder.getFields(CLASS_NAME)).thenReturn(singletonList(field));

        FieldMetadata fmd = mock(FieldMetadata.class);
        when(fmd.getName()).thenReturn("notInDefFg");
        when(classMetadata.newFieldMetadata("notInDefFg")).thenReturn(fmd);

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class, schemaHolder);

        verify(fmd, never()).setDefaultFetchGroup(anyBoolean());
    }

    @Test
    public void shouldMarkEudeFieldsAsUnique() {
        when(entity.getName()).thenReturn(ENTITY_NAME);
        when(jdoMetadata.newPackageMetadata(anyString())).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(anyString())).thenReturn(classMetadata);

        FieldDto eudeField = mock(FieldDto.class);
        FieldBasicDto eudeBasic = mock(FieldBasicDto.class);
        when(eudeField.getBasic()).thenReturn(eudeBasic);
        when(eudeBasic.getName()).thenReturn("uniqueField");
        when(eudeField.isReadOnly()).thenReturn(false);
        when(eudeBasic.isUnique()).thenReturn(true);
        when(eudeField.getType()).thenReturn(TypeDto.STRING);

        FieldDto ddeField = mock(FieldDto.class);
        FieldBasicDto ddeBasic = mock(FieldBasicDto.class);
        when(ddeField.getBasic()).thenReturn(ddeBasic);
        when(ddeBasic.getName()).thenReturn("uniqueField2");
        when(ddeField.isReadOnly()).thenReturn(true);
        when(ddeBasic.isUnique()).thenReturn(true);
        when(ddeField.getType()).thenReturn(TypeDto.STRING);

        when(schemaHolder.getFields(entity)).thenReturn(asList(ddeField, eudeField));

        FieldMetadata fmdEude = mock(FieldMetadata.class);
        when(fmdEude.getName()).thenReturn("uniqueField");
        when(classMetadata.newFieldMetadata("uniqueField")).thenReturn(fmdEude);

        FieldMetadata fmdDde = mock(FieldMetadata.class);
        when(fmdDde.getName()).thenReturn("uniqueField2");
        when(classMetadata.newFieldMetadata("uniqueField2")).thenReturn(fmdDde);

        UniqueMetadata umd = mock(UniqueMetadata.class);
        when(fmdEude.newUniqueMetadata()).thenReturn(umd);

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, Sample.class, schemaHolder);

        verify(fmdDde, never()).newUniqueMetadata();
        verify(fmdDde, never()).setUnique(anyBoolean());
        verify(fmdEude).newUniqueMetadata();
        verify(umd).setName("unq_Sample_uniqueField");
    }

    @Test
    public void shouldSetIncrementStrategy() {
        when(entity.getName()).thenReturn(ENTITY_NAME);
        when(entity.getModule()).thenReturn(MODULE);
        when(entity.getNamespace()).thenReturn(NAMESPACE);
        when(entity.getTableName()).thenReturn(TABLE_NAME);
        when(jdoMetadata.newPackageMetadata(PACKAGE)).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(ENTITY_NAME)).thenReturn(classMetadata);

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity, SampleWithIncrementStrategy.class, schemaHolder);

        verify(jdoMetadata).newPackageMetadata(PACKAGE);
        verify(packageMetadata).newClassMetadata(ENTITY_NAME);
        verify(classMetadata).setTable(TABLE_NAME_3);
        verifyCommonClassMetadata(IdGeneratorStrategy.INCREMENT);
    }

    private void verifyCommonClassMetadata() {
        verifyCommonClassMetadata(IdGeneratorStrategy.NATIVE);
    }

    private void verifyCommonClassMetadata(IdGeneratorStrategy expextedStrategy) {
        verify(classMetadata).setDetachable(true);
        verify(classMetadata).setIdentityType(IdentityType.APPLICATION);
        verify(classMetadata).setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);
        verify(idMetadata).setPrimaryKey(true);
        verify(idMetadata).setValueStrategy(expextedStrategy);
        verify(inheritanceMetadata).setCustomStrategy("complete-table");
    }
}
