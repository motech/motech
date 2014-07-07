package org.motechproject.mds.util;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a helper class, used while generating enums in MDS.
 */
public final class EnumHelper {

    private EnumHelper() {
    }

    /**
     * Prefixes enum values, if they start with illegal character
     * (other than letter, dollar sign or underscore
     *
     * @param value  an enum value
     * @return       either the same value, if the value is legal,
     *               or value prefixed with underscore, if the value is illegal
     */
    public static String prefixEnumValue(String value) {
        return value.startsWith("$") || value.startsWith("_") || startsWithLetter(value) ? value : "_".concat(value);
    }

    /**
     * Prefixes a list of enum values. For each value in the list, <code>prefixEnumValue(java.lang.String)</code>
     * is called.
     *
     * @param values  a list of enum values
     * @return        a list of prefixed values
     */
    public static List<String> prefixEnumValues(List<String> values) {
        List<String> prefixedValues = new ArrayList<>();
        for (String value : values) {
            prefixedValues.add(prefixEnumValue(value));
        }

        return prefixedValues;
    }

    private static boolean startsWithLetter(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        char firstChar = str.charAt(0);
        return firstChar >= 'A' && firstChar <= 'z';
    }
}
