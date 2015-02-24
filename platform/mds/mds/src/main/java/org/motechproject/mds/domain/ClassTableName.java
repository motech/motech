package org.motechproject.mds.domain;

import org.motechproject.mds.util.ClassName;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;


public final class ClassTableName {

    private ClassTableName() {

    }

    public static String getTableName(String table, String suffix) {
        String tableName = table;

        if (isNotBlank(suffix)) {
            tableName += "_" + suffix;
        }

        return tableName.replace('-', '_').replace(' ', '_').toUpperCase();
    }

    public static String getTableName(Entity entity) {
        return getTableName(entity, EntityType.STANDARD);
    }

    public static String getTableName(Entity entity, EntityType type) {
        String tableName = getTableName(entity.getClassName(), entity.getModule(), entity.getNamespace(), entity.getTableName(), type);
        if (type == EntityType.STANDARD) {
            return tableName;
        }
        return getTableName(tableName, "_" + type.toString());
    }

    public static String getTableName(String className, String module, String namespace, String tableName, EntityType entityType) {
        String simpleName = ClassName.getSimpleName(className);
        String mod = defaultIfBlank(module, "MDS");
        String table = defaultIfBlank(tableName, "");

        StringBuilder builder = new StringBuilder();
        if (table.isEmpty()) {
            builder.append(mod).append("_");

            if (isNotBlank(namespace)) {
                builder.append(namespace).append("_");
            }

            builder.append(simpleName);

            return builder.toString().replace('-', '_').replace(' ', '_').toUpperCase();
        } else {
            builder.append(table);

            if (entityType != null && !EntityType.STANDARD.equals(entityType)) {
                builder.append("__").append(entityType.toString());
            }

            return builder.toString();
        }
    }
}
