package org.motechproject.commons.date.util;

public final class StringUtil {
    private StringUtil() {
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
