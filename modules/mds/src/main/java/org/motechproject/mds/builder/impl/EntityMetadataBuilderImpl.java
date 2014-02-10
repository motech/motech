package org.motechproject.mds.builder.impl;

import org.motechproject.mds.builder.EntityMetadataBuilder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.util.ClassName;
import org.springframework.stereotype.Component;

import javax.jdo.annotations.IdentityType;
import javax.jdo.metadata.ClassMetadata;
import javax.jdo.metadata.ClassPersistenceModifier;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.PackageMetadata;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * The <code>EntityMetadataBuilderImpl</code> class is responsible for building jdo metadata for an
 * entity class.
 */
@Component
public class EntityMetadataBuilderImpl implements EntityMetadataBuilder {

    @Override
    public JDOMetadata createBaseEntity(JDOMetadata md, Entity entity) {
        PackageMetadata pmd = md.newPackageMetadata(ClassName.getPackage(entity.getClassName()));
        ClassMetadata cmd = pmd.newClassMetadata(ClassName.getSimpleName(entity.getClassName()));

        cmd.setTable(getTableName(entity));
        cmd.setDetachable(true);
        cmd.setIdentityType(IdentityType.DATASTORE);
        cmd.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);

        return md;
    }

    private static String getTableName(Entity entity) {
        String simpleName = ClassName.getSimpleName(entity.getClassName());
        String module = entity.getModule();
        String namespace = entity.getNamespace();

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

}
