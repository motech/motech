package org.motechproject.commons.date.util;

/**
 * Utility class for {@code String} class.
 */
public final class StringUtil {

    /**
     * This is a utility class and should not be instantiated
     */
    private StringUtil() {
    }

    /**
     * Checks if given string is null or empty.
     *
     * @param str  string to be checked
     * @return true if given {@code String} is empty or null, false otherwise
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
