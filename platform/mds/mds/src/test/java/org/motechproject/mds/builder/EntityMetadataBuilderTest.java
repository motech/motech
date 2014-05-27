package org.motechproject.mds.builder;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.mds.builder.impl.EntityMetadataBuilderImpl;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.domain.relationships.OneToManyRelationship;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.util.Constants;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.metadata.ClassMetadata;
import javax.jdo.metadata.ClassPersistenceModifier;
import javax.jdo.metadata.CollectionMetadata;
import javax.jdo.metadata.FieldMetadata;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.PackageMetadata;

import java.util.Arrays;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

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
    public void shouldAddRelationshipMetadata() {
        Field oneToManyField = mock(Field.class);
        when(oneToManyField.getName()).thenReturn("oneToManyName");
        org.motechproject.mds.domain.FieldMetadata entityFmd = mock(org.motechproject.mds.domain.FieldMetadata.class);
        when(entityFmd.getValue()).thenReturn("org.motechproject.test.MyClass");
        when(oneToManyField.getMetadata(Constants.MetadataKeys.RELATED_CLASS)).thenReturn(entityFmd);
        Type oneToManyType = mock(Type.class);
        when(oneToManyType.getTypeClass()).thenReturn((Class) OneToManyRelationship.class);
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

    private void verifyCommonClassMetadata() {
        verify(classMetadata).setDetachable(true);
        verify(classMetadata).setIdentityType(IdentityType.APPLICATION);
        verify(classMetadata).setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);
        verify(idMetadata).setPrimaryKey(true);
        verify(idMetadata).setValueStrategy(IdGeneratorStrategy.INCREMENT);
    }
}
