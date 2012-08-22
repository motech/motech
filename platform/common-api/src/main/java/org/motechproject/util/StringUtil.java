package org.motechproject.util;

public final class StringUtil {
    private StringUtil() {
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
