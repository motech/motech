package org.motechproject.mds.factory;

import junit.framework.Assert;
import org.datanucleus.api.jdo.metadata.JDOMetadataImpl;
import org.junit.Test;

import javax.jdo.metadata.ClassMetadata;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.PackageMetadata;

import static javax.jdo.annotations.IdentityType.DATASTORE;
import static javax.jdo.metadata.ClassPersistenceModifier.PERSISTENCE_CAPABLE;

public class EntityMetadataFactoryTest {

    @Test
    public void testCreateBaseEntity() throws Exception {
        String packageName = "org.motechproject";
        String simpleName = "Sample";
        String className = String.format("%s.%s", packageName, simpleName);
        JDOMetadata mainMetadata = new JDOMetadataImpl();

        EntityMetadataFactory.createBaseEntity(mainMetadata, className);

        Assert.assertEquals(1, mainMetadata.getNumberOfPackages());

        PackageMetadata packageMetadata = mainMetadata.getPackages()[0];

        Assert.assertEquals(packageName, packageMetadata.getName());
        Assert.assertEquals(1, packageMetadata.getNumberOfClasses());

        ClassMetadata classMetadata = packageMetadata.getClasses()[0];

        Assert.assertEquals(simpleName, classMetadata.getName());
        Assert.assertEquals(simpleName, classMetadata.getTable());
        Assert.assertTrue(classMetadata.getDetachable());
        Assert.assertEquals(DATASTORE, classMetadata.getIdentityType());
        Assert.assertEquals(PERSISTENCE_CAPABLE, classMetadata.getPersistenceModifier());

    }
}
