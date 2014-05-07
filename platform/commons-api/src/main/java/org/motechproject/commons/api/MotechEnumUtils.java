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
     * Returns a csv string given a set of enums
     *
     * @param items a set of enums
     * @return the csv string constructed from the given enum set
     */
    public static String toString(Set<? extends Enum> items) {
        StringBuilder csv = new StringBuilder();
        Boolean first = true;
        for (Enum item : items) {
            if (first) {
                first = false;
            } else {
                csv.append(',');
            }

            csv.append(item.name());
        }
        return csv.toString();
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
            set.add(toEnum(enumClass, string));
        }

        return set;
    }

    /**
     * Returns a set of enums given a comma separated string and an enum class
     *
     * @param enumClass the enum class
     * @param csv a comma separated string representing a set of enum values
     * @return the enum set constructed from the given string
     */
    public static <T extends Enum> Set<T> toEnumSet(Class<T> enumClass, String csv) {
        Set<T> set = new HashSet<>();
        if (csv != null) {
            String[] strings = csv.split(",");

            for (String string : strings) {
                set.add(toEnum(enumClass, string));
            }
        }

        return set;
    }

    /**
     * Returns an enum value given a string value and an enum class
     *
     * @param enumClass the enum class
     * @param string the string representation of the enum value
     * @return the enum value corresponding to the given string
     * @throws java.lang.IllegalStateException if the value doesn't match a valid enum
     */
    private static <T extends Enum> T toEnum(Class<T> enumClass, String string) {
        if (StringUtils.isNotBlank(string)) {
            for (T enumVal : enumClass.getEnumConstants()) {
                if (enumVal.name().equalsIgnoreCase(string)) {
                    return enumVal;
                }
            }
        }

        throw new IllegalStateException("Invalid Enum Value: '" + string + "' is not a valid value for '" +
                enumClass.getName() + "'");
    }
}
