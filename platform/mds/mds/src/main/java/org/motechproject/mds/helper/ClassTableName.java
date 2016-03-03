package org.motechproject.mds.helper;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.util.ClassName;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Util class, that provides methods connected to the table name generation.
 */
public final class ClassTableName {

    private ClassTableName() {
    }

    /**
     * Builds table name for the underlying database, based on the provided values. Replaces all occurences
     * of hyphen ("-") and space (" ") with the underscore ("_") and makes all characters uppercase.
     *
     * @param table the base table name
     * @param suffix suffix to use, after the base name
     * @return parsed table name
     */
    public static String getTableName(String table, String suffix) {
        String tableName = table;

        if (isNotBlank(suffix)) {
            tableName += "_" + suffix;
        }

        return tableName.replace('-', '_').replace(' ', '_').toUpperCase();
    }

    /**
     * Builds table name for the underlying database, based on the given entity. Replaces all occurences
     * of hyphen ("-") and space (" ") with the underscore ("_") and makes all characters uppercase.
     *
     * @param entity entity to build table name for
     * @return parsed table name
     */
    public static String getTableName(Entity entity) {
        return getTableName(entity, EntityType.STANDARD);
    }

    /**
     * Builds table name for the underlying database, based on the given entity. Replaces all occurences
     * of hyphen ("-") and space (" ") with the underscore ("_") and makes all characters uppercase.
     *
     * @param entity entity to build table name for
     * @param type the type of an entity; will be added to the end of the name, if other than "STANDARD"
     * @return parsed table name
     */
    public static String getTableName(Entity entity, EntityType type) {
        String tableName = getTableName(entity.getClassName(), entity.getModule(), entity.getNamespace(), entity.getTableName(), type);
        if (type == EntityType.STANDARD) {
            return tableName;
        }
        return getTableName(tableName, "_" + type.toString());
    }

    /**
     * Builds table name for the underlying database, based on the provided values. Replaces all occurences
     * of hyphen ("-") and space (" ") with the underscore ("_") and makes all characters uppercase.
     *
     * @param className fully qualified or simple name of the class
     * @param module entity module (defaults to "MDS")
     * @param namespace namespace of the entity
     * @param tableName base table name; if not empty, this method will simply append the type of an entity to the base name
     * @param entityType the type of the entity; will be added to the end of the name, if other than "STANDARD"
     * @return parsed table name
     */
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
