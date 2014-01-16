package org.motechproject.mds.builder;

import org.motechproject.mds.domain.EntityMapping;

import javax.jdo.annotations.IdentityType;
import javax.jdo.metadata.ClassMetadata;
import javax.jdo.metadata.ClassPersistenceModifier;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.PackageMetadata;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * The <code>EntityMetadataBuilder</code> class is responsible to create a jdo metadata about
 * entity class.
 */
public final class EntityMetadataBuilder {

    private EntityMetadataBuilder() {
    }

    /**
     * Add to the empty {@link javax.jdo.metadata.JDOMetadata} information about package and
     * class name.
     *
     * @param md      a empty instance of {@link javax.jdo.metadata.JDOMetadata}.
     * @param mapping a instance of {@link org.motechproject.mds.domain.EntityMapping}
     * @return an instance of {@link javax.jdo.metadata.JDOMetadata} with information about package
     * and class name.
     */
    public static JDOMetadata createBaseEntity(JDOMetadata md, EntityMapping mapping) {
        PackageMetadata pmd = md.newPackageMetadata(getPackage(mapping.getClassName()));
        ClassMetadata cmd = pmd.newClassMetadata(getSimpleName(mapping.getClassName()));

        cmd.setTable(getTableName(mapping));
        cmd.setDetachable(true);
        cmd.setIdentityType(IdentityType.DATASTORE);
        cmd.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);

        return md;
    }

    private static String getTableName(EntityMapping mapping) {
        String simpleName = getSimpleName(mapping.getClassName());
        String module = mapping.getModule();
        String namespace = mapping.getNamespace();

        StringBuilder builder = new StringBuilder();
        if (isNotBlank(module)) {
            builder.append(module).append("_");
        }

        if (isNotBlank(namespace)) {
            builder.append(namespace).append("_");
        }

        builder.append(simpleName);

        return builder.toString().toUpperCase();
    }

    private static String getPackage(String className) {
        int idx = className.lastIndexOf('.');
        return idx < 0 ? EntityBuilder.PACKAGE : className.substring(0, idx);
    }

    private static String getSimpleName(String className) {
        int idx = className.lastIndexOf('.');
        return idx < 0 ? className : className.substring(idx + 1);
    }

}
