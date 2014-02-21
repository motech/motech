package org.motechproject.mds.builder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.builder.impl.EntityMetadataBuilderImpl;
import org.motechproject.mds.domain.Entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.metadata.ClassMetadata;
import javax.jdo.metadata.ClassPersistenceModifier;
import javax.jdo.metadata.FieldMetadata;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.PackageMetadata;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EntityMetadataBuilderTest {
    private static final String PACKAGE = "org.motechproject.mds.entity";
    private static final String ENTITY_NAME = "Sample";
    private static final String MODULE = "MrS";
    private static final String NAMESPACE = "arrio";

    private static final String CLASS_NAME = String.format("%s.%s", PACKAGE, ENTITY_NAME);
    private static final String TABLE_NAME_1 = ENTITY_NAME.toUpperCase();
    private static final String TABLE_NAME_2 = String.format("%s_%s", MODULE, ENTITY_NAME).toUpperCase();
    private static final String TABLE_NAME_3 = String.format("%s_%s_%s", MODULE, NAMESPACE, ENTITY_NAME).toUpperCase();

    private EntityMetadataBuilder entityMetadataBuilder = new EntityMetadataBuilderImpl();

    @Mock
    private Entity entity;

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
        when(entity.getClassName()).thenReturn(CLASS_NAME);
        when(classMetadata.newFieldMetadata("id")).thenReturn(idMetadata);
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

    private void verifyCommonClassMetadata() {
        verify(classMetadata).setDetachable(true);
        verify(classMetadata).setIdentityType(IdentityType.APPLICATION);
        verify(classMetadata).setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);
        verify(idMetadata).setPrimaryKey(true);
        verify(idMetadata).setValueStrategy(IdGeneratorStrategy.IDENTITY);
    }
}
