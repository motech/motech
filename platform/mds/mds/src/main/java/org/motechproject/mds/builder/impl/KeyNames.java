package org.motechproject.mds.builder.impl;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class for generating key names in MDS.
 */
public final class KeyNames {

    public static String uniqueKeyName(String entityName, String fieldName) {
        // unique keys are only used for standard tables, history and trash don't use them
        return join("unq", entityName, fieldName);
    }

    private static String join(Object... parts) {
        return StringUtils.join(parts, '_');
    }

    private KeyNames() {
    }
}
