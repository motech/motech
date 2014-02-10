package org.motechproject.mds.builder;

import org.datanucleus.api.jdo.metadata.JDOMetadataImpl;
import org.junit.Test;
import org.motechproject.mds.builder.impl.EntityMetadataBuilderImpl;
import org.motechproject.mds.domain.Entity;

import javax.jdo.metadata.ClassMetadata;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.PackageMetadata;

import static javax.jdo.annotations.IdentityType.DATASTORE;
import static javax.jdo.metadata.ClassPersistenceModifier.PERSISTENCE_CAPABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EntityMetadataBuilderTest {
    private static final String PACKAGE = "org.motechproject";
    private static final String ENTITY_NAME = "Sample";
    private static final String MODULE = "MrS";
    private static final String NAMESPACE = "arrio";

    private static final String CLASS_NAME = String.format("%s.%s", PACKAGE, ENTITY_NAME);
    private static final String TABLE_NAME_1 = ENTITY_NAME.toUpperCase();
    private static final String TABLE_NAME_2 = String.format("%s_%s", MODULE, ENTITY_NAME).toUpperCase();
    private static final String TABLE_NAME_3 = String.format("%s_%s_%s", MODULE, NAMESPACE, ENTITY_NAME).toUpperCase();

    private EntityMetadataBuilder entityMetadataBuilder = new EntityMetadataBuilderImpl();

    @Test
    public void shouldCreateBaseEntity() throws Exception {
        JDOMetadata mainMetadata = new JDOMetadataImpl();
        Entity entity = new Entity();
        entity.setClassName(CLASS_NAME);

        entityMetadataBuilder.createBaseEntity(mainMetadata, entity);

        assertEquals(1, mainMetadata.getNumberOfPackages());

        PackageMetadata packageMetadata = mainMetadata.getPackages()[0];

        assertEquals(PACKAGE, packageMetadata.getName());
        assertEquals(1, packageMetadata.getNumberOfClasses());

        ClassMetadata classMetadata = packageMetadata.getClasses()[0];

        assertEquals(ENTITY_NAME, classMetadata.getName());
        assertEquals(TABLE_NAME_1, classMetadata.getTable());
        assertTrue(classMetadata.getDetachable());
        assertEquals(DATASTORE, classMetadata.getIdentityType());
        assertEquals(PERSISTENCE_CAPABLE, classMetadata.getPersistenceModifier());
    }

    @Test
    public void shouldSetAppropriateTableName() throws Exception {
        assertEquals(TABLE_NAME_1, getClassMetadata(null, null).getTable());
        assertEquals(TABLE_NAME_2, getClassMetadata(MODULE, null).getTable());
        assertEquals(TABLE_NAME_3, getClassMetadata(MODULE, NAMESPACE).getTable());
    }

    private ClassMetadata getClassMetadata(String module, String namespace) {
        JDOMetadata mainMetadata = new JDOMetadataImpl();
        Entity entity = new Entity();
        entity.setClassName(CLASS_NAME);
        entity.setModule(module);
        entity.setNamespace(namespace);

        entityMetadataBuilder.createBaseEntity(mainMetadata, entity);
        return mainMetadata.getPackages()[0].getClasses()[0];
    }
}
