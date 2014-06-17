package org.motechproject.mds.builder;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.motechproject.mds.builder.impl.EntityMetadataBuilderImpl;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
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
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.PackageMetadata;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_CLASS;
import static org.motechproject.mds.util.Constants.Util.CREATION_DATE_DISPLAY_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.CREATION_DATE_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.CREATOR_DISPLAY_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.CREATOR_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFICATION_DATE_DISPLAY_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFICATION_DATE_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFIED_BY_DISPLAY_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFIED_BY_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.OWNER_DISPLAY_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.OWNER_FIELD_NAME;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MotechClassPool.class)
public class EntityMetadataBuilderTest {
    private static final String PACKAGE = "org.motechproject.mds.entity";
    private static final String ENTITY_NAME = "Sample";
    private static final String MODULE = "MrS";
    private static final String NAMESPACE = "arrio";

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

    @Before
    public void setUp() {
        initMocks(this);

        when(entity.getClassName()).thenReturn(CLASS_NAME);
        when(classMetadata.newFieldMetadata("id")).thenReturn(idMetadata);
        when(entity.getField("id")).thenReturn(idField);
    }

    @Test
    public void shouldAddEntityMetadata() throws Exception {
        when(entity.getModule()).thenReturn(MODULE);
        when(entity.getNamespace()).thenReturn(NAMESPACE);
        when(jdoMetadata.newPackageMetadata(PACKAGE)).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(ENTITY_NAME)).thenReturn(classMetadata);

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity);

        verify(jdoMetadata).newPackageMetadata(PACKAGE);
        verify(packageMetadata).newClassMetadata(ENTITY_NAME);
        verify(classMetadata).setTable(TABLE_NAME_3);
        verifyCommonClassMetadata();
    }

    @Test
    public void shouldAddToAnExistingPackage() {
        when(jdoMetadata.getPackages()).thenReturn(new PackageMetadata[]{packageMetadata});
        when(packageMetadata.getName()).thenReturn(PACKAGE);
        when(packageMetadata.newClassMetadata(ENTITY_NAME)).thenReturn(classMetadata);

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity);

        verify(jdoMetadata, never()).newPackageMetadata(PACKAGE);
        verify(jdoMetadata).getPackages();
        verify(packageMetadata).newClassMetadata(ENTITY_NAME);
        verify(classMetadata).setTable(TABLE_NAME_1);
        verifyCommonClassMetadata();
    }

    @Test
    public void shouldSetAppropriateTableName() throws Exception {
        when(jdoMetadata.newPackageMetadata(anyString())).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(anyString())).thenReturn(classMetadata);

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity);
        verify(classMetadata).setTable(TABLE_NAME_1);

        when(entity.getModule()).thenReturn(MODULE);
        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity);
        verify(classMetadata).setTable(TABLE_NAME_2);

        when(entity.getNamespace()).thenReturn(NAMESPACE);
        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity);
        verify(classMetadata).setTable(TABLE_NAME_3);
    }

    @Test
    public void shouldAddBaseEntityMetadata() throws Exception {
        CtField ctField = mock(CtField.class);
        CtClass ctClass = mock(CtClass.class);
        ClassData classData = mock(ClassData.class);
        ClassPool pool = mock(ClassPool.class);

        PowerMockito.mockStatic(MotechClassPool.class);
        PowerMockito.when(MotechClassPool.getDefault()).thenReturn(pool);

        when(classData.getClassName()).thenReturn(CLASS_NAME);
        when(classData.getModule()).thenReturn(MODULE);
        when(classData.getNamespace()).thenReturn(NAMESPACE);
        when(pool.getOrNull(CLASS_NAME)).thenReturn(ctClass);
        when(ctClass.getDeclaredField("id")).thenReturn(ctField);
        when(jdoMetadata.newPackageMetadata(PACKAGE)).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(ENTITY_NAME)).thenReturn(classMetadata);

        entityMetadataBuilder.addBaseMetadata(jdoMetadata, classData);

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
        when(oneToManyField.getMetadata()).thenReturn(Arrays.asList(entityFmd));
        when(oneToManyField.getType()).thenReturn(oneToManyType);

        FieldMetadata fmd = mock(FieldMetadata.class);
        CollectionMetadata collMd = mock(CollectionMetadata.class);

        when(entity.getFields()).thenReturn(Arrays.asList(oneToManyField));
        when(jdoMetadata.newPackageMetadata(PACKAGE)).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(ENTITY_NAME)).thenReturn(classMetadata);
        when(classMetadata.newFieldMetadata("oneToManyName")).thenReturn(fmd);
        when(fmd.getCollectionMetadata()).thenReturn(collMd);

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity);

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
        org.motechproject.mds.domain.FieldMetadata entityFmd = mock(org.motechproject.mds.domain.FieldMetadata.class);
        Type oneToOneType = mock(Type.class);

        when(entityFmd.getKey()).thenReturn(RELATED_CLASS);
        when(entityFmd.getValue()).thenReturn(myClassName);

        when(oneToOneType.getTypeClass()).thenReturn((Class) OneToOneRelationship.class);
        when(oneToOneType.isRelationship()).thenReturn(true);

        when(oneToOneField.getName()).thenReturn("oneToOneName");
        when(oneToOneField.getMetadata()).thenReturn(Arrays.asList(entityFmd));
        when(oneToOneField.getType()).thenReturn(oneToOneType);

        FieldMetadata fmd = mock(FieldMetadata.class);

        when(entity.getFields()).thenReturn(Arrays.asList(oneToOneField));
        when(jdoMetadata.newPackageMetadata(PACKAGE)).thenReturn(packageMetadata);
        when(packageMetadata.newClassMetadata(ENTITY_NAME)).thenReturn(classMetadata);
        when(classMetadata.newFieldMetadata("oneToOneName")).thenReturn(fmd);

        /* We simulate configuration for the bi-directional relationship (the related class has got
           a field that links back to the main class) */

        CtClass myClass = mock(CtClass.class);
        CtClass relatedClass = mock(CtClass.class);
        CtField myField = mock(CtField.class);
        CtField relatedField = mock(CtField.class);

        ClassPool pool = mock(ClassPool.class);
        PowerMockito.mockStatic(MotechClassPool.class);
        PowerMockito.when(MotechClassPool.getDefault()).thenReturn(pool);
        when(pool.get(myClassName)).thenReturn(myClass);
        when(pool.get(CLASS_NAME)).thenReturn(relatedClass);

        when(myClass.getName()).thenReturn(myClassName);
        when(myClass.getDeclaredFields()).thenReturn(new CtField[]{myField});

        when(myField.getType()).thenReturn(relatedClass);
        when(myField.getName()).thenReturn(myFieldName);

        when(relatedClass.getDeclaredFields()).thenReturn(new CtField[]{relatedField});
        when(relatedClass.getName()).thenReturn(CLASS_NAME);

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity);

        verifyCommonClassMetadata();
        verify(fmd).setDefaultFetchGroup(true);
        verify(fmd).setMappedBy(myFieldName);
        verify(fmd).setPersistenceModifier(PersistenceModifier.PERSISTENT);
    }

    @Test
    public void shouldAddObjectValueGeneratorToAppropriateFields() throws Exception {
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

        entityMetadataBuilder.addEntityMetadata(jdoMetadata, entity);

        for (FieldMetadata metadata : list) {
            String name = metadata.getName();
            // the id field should not have set object value generator metadata
            int invocations = "id".equalsIgnoreCase(name) ? 0 : 1;

            verify(classMetadata).newFieldMetadata(name);

            verify(metadata, times(invocations)).setPersistenceModifier(PersistenceModifier.PERSISTENT);
            verify(metadata, times(invocations)).setDefaultFetchGroup(true);
            verify(metadata, times(invocations)).newExtensionMetadata("datanucleus", "object-value-generator", "ovg." + name);
        }
    }

    private void verifyCommonClassMetadata() {
        verify(classMetadata).setDetachable(true);
        verify(classMetadata).setIdentityType(IdentityType.APPLICATION);
        verify(classMetadata).setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);
        verify(idMetadata).setPrimaryKey(true);
        verify(idMetadata).setValueStrategy(IdGeneratorStrategy.INCREMENT);
    }
}
