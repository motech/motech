package org.motechproject.mds.factory;

import javax.jdo.annotations.IdentityType;
import javax.jdo.metadata.ClassMetadata;
import javax.jdo.metadata.ClassPersistenceModifier;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.PackageMetadata;

/**
 * The <code>EntityMetadataFactory</code> class is responsible to create a jdo metadata about
 * entity class.
 */
public final class EntityMetadataFactory {

    private EntityMetadataFactory() {
    }

    /**
     * Add to the empty {@link javax.jdo.metadata.JDOMetadata} information about package and
     * class name.
     *
     * @param md        a empty instance of {@link javax.jdo.metadata.JDOMetadata}.
     * @param className a fully qualified class name.
     * @return an instance of {@link javax.jdo.metadata.JDOMetadata} with information about package
     * and class name.
     */
    public static JDOMetadata createBaseEntity(JDOMetadata md, String className) {
        String packageName = className.substring(0, className.lastIndexOf('.'));
        String simpleName = className.substring(className.lastIndexOf('.') + 1);

        PackageMetadata pmd = md.newPackageMetadata(packageName);
        ClassMetadata cmd = pmd.newClassMetadata(simpleName);

        cmd.setTable(simpleName);
        cmd.setDetachable(true);
        cmd.setIdentityType(IdentityType.DATASTORE);
        cmd.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);

        return md;
    }

}
