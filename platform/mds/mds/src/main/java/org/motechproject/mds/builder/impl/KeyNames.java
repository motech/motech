package org.motechproject.mds.builder.impl;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.domain.EntityType;

/**
 * Utility class for generating key names in MDS.
 */
public final class KeyNames {

    public static String lookupIndexKeyName(String entityName, Long entityId, String fieldName, EntityType entityType) {
        return join("lkp_idx", entityType.getName(entityName), fieldName, entityId);
    }

    public static String foreignKeyName(String entityName, Long entityId, String fieldName, EntityType entityType) {
        return join("fk", entityType.getName(entityName), fieldName, entityId);
    }

    public static String mapForeignKeyName(String entityName, Long entityId, String fieldName, EntityType entityType) {
        return join("map_fk", entityType.getName(entityName), fieldName, entityId);
    }

    public static String cbForeignKeyName(String entityName, Long entityId, String fieldName, EntityType entityType) {
        return join("cb_fk", entityType.getName(entityName), fieldName, entityId);
    }

    private static String join(Object... parts) {
        return StringUtils.join(parts, '_');
    }

    private KeyNames() {
    }
}
