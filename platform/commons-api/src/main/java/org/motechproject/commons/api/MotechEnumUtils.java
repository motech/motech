package org.motechproject.commons.api;

import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Misc enum-related helper functions
 */
public final class MotechEnumUtils {

    /**
     * This is a utility class and should not be instantiated
     */
    private MotechEnumUtils() { }

    /**
     * Returns a set of strings given a set of enums
     *
     * @param items a set of enums
     * @return the string set constructed from the given enum set
     */
    public static Set<String> toStringSet(Set<? extends Enum> items) {
        Set<String> itemStringSet = new HashSet<>();
        for (Enum item : items) {
            itemStringSet.add(item.name());
        }
        return itemStringSet;
    }

    /**
     * Returns a set of enums given a set of strings and an enum class
     *
     * @param enumClass the enum class
     * @param strings a set of strings
     * @return the enum set constructed from the given string set
     */
    public static <T extends Enum> Set<T> toEnumSet(Class<T> enumClass, Set<String> strings) {
        Set<T> set = new HashSet<>();

        for (String string : strings) {
            set.add(asEnum(enumClass, string));
        }

        return set;
    }

    private static <T extends Enum> T asEnum(Class<T> enumClass, String string) {
        T result = null;

        if (StringUtils.isNotBlank(string)) {
            for (T status : enumClass.getEnumConstants()) {
                if (status.name().equalsIgnoreCase(string)) {
                    result = status;
                    break;
                }
            }
        }

        return result;
    }
}
