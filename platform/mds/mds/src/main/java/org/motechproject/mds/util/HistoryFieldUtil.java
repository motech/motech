package org.motechproject.mds.util;

import static org.apache.commons.lang.StringUtils.uncapitalize;

/**
 * The <code>HistoryFieldUtil</code> class provides helper methods to determine
 * field names in the given history class.
 */
public final class HistoryFieldUtil {

    private HistoryFieldUtil() {
    }

    public static String currentVersion(Class<?> historyClass) {
        return uncapitalize(historyClass.getSimpleName() + "CurrentVersion");
    }

    public static String schemaVersion(Class<?> historyClass) {
        return uncapitalize(historyClass.getSimpleName() + "SchemaVersion");
    }

    public static String trashFlag(Class<?> historyClass) {
        return uncapitalize(historyClass.getSimpleName() + "FromTrash");
    }

}
