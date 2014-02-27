package org.motechproject.mds.builder.impl;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.builder.EntityMetadataBuilder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.util.ClassName;
import org.springframework.stereotype.Component;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.metadata.ClassMetadata;
import javax.jdo.metadata.ClassPersistenceModifier;
import javax.jdo.metadata.FieldMetadata;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.PackageMetadata;
import java.util.List;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * The <code>EntityMetadataBuilderImpl</code> class is responsible for building jdo metadata for an
 * entity class.
 */
@Component
public class EntityMetadataBuilderImpl implements EntityMetadataBuilder {

    @Override
    public void addEntityMetadata(JDOMetadata jdoMetadata, Entity entity) {
        String packageName = ClassName.getPackage(ClassName.getEntityName(entity.getClassName()));

        PackageMetadata pmd = getPackageMetadata(jdoMetadata, packageName);
        ClassMetadata cmd = getClassMetadata(pmd, ClassName.getSimpleName(ClassName.getEntityName(entity.getClassName())));

        cmd.setTable(getTableName(entity));
        cmd.setDetachable(true);
        cmd.setIdentityType(IdentityType.APPLICATION);
        cmd.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);

        addIdField(cmd, entity);

        for (Field field : entity.getFields()) {
            // we want to fetch lists and Time in the default group so they are attached
            // to detached objects
            Class<?> typeClass = field.getType().getTypeClass();
            if (List.class.isAssignableFrom(typeClass)) {
                FieldMetadata fmd = cmd.newFieldMetadata(field.getName());
                fmd.setDefaultFetchGroup(true);
            } else if (Time.class.isAssignableFrom(typeClass)) {
                // for time we register our converter which persists as string
                FieldMetadata fmd = cmd.newFieldMetadata(field.getName());
                fmd.setPersistenceModifier(PersistenceModifier.PERSISTENT);
                fmd.setDefaultFetchGroup(true);
                fmd.newExtensionMetadata("datanucleus", "type-converter-name", "dn.time-string");
            }
        }
    }

    private static ClassMetadata getClassMetadata(PackageMetadata pmd, String className) {
        ClassMetadata[] classes = pmd.getClasses();
        if (ArrayUtils.isNotEmpty(classes)) {
            for (ClassMetadata cmd : classes) {
                if (StringUtils.equals(className, cmd.getName())) {
                    return cmd;
                }
            }
        }
        return pmd.newClassMetadata(className);
    }

    private static PackageMetadata getPackageMetadata(JDOMetadata jdoMetadata, String packageName) {
        // first look for existing metadata
        PackageMetadata[] packages = jdoMetadata.getPackages();
        if (ArrayUtils.isNotEmpty(packages)) {
            for (PackageMetadata pkgMetadata : packages) {
                if (StringUtils.equals(pkgMetadata.getName(), packageName)) {
                    return pkgMetadata;
                }
            }
        }
        // if not found, create new
        return jdoMetadata.newPackageMetadata(packageName);
    }

    private static String getTableName(Entity entity) {
        String simpleName = ClassName.getSimpleName(entity.getClassName());
        String module = defaultIfBlank(entity.getModule(), "MDS");
        String namespace = entity.getNamespace();

        StringBuilder builder = new StringBuilder();
        builder.append(module).append("_");

        if (isNotBlank(namespace)) {
            builder.append(namespace).append("_");
        }

        builder.append(simpleName);

        return builder.toString().replace(' ', '_').toUpperCase();
    }

    private void addIdField(ClassMetadata cmd, Entity entity) {
        if (null != entity.getField("id")) {
            FieldMetadata metadata = cmd.newFieldMetadata("id");
            metadata.setValueStrategy(IdGeneratorStrategy.IDENTITY);
            metadata.setPrimaryKey(true);
            metadata.setIndexed(true);
            metadata.setUnique(true);
        }
    }

}
