package org.motechproject.mds.util;

import static org.apache.commons.lang.StringUtils.uncapitalize;

/**
 * Contains utility methods for dealing with history and trash clasess
 */
public final class HistoryTrashClassHelper {

    public static String currentVersion(Class<?> historyClass) {
        return uncapitalize(historyClass.getSimpleName() + "CurrentVersion");
    }

    public static String schemaVersion(Class<?> historyClass) {
        return uncapitalize(historyClass.getSimpleName() + "SchemaVersion");
    }

    public static String trashFlag(Class<?> historyClass) {
        return uncapitalize(historyClass.getSimpleName() + "FromTrash");
    }

    private HistoryTrashClassHelper() {
    }
}
