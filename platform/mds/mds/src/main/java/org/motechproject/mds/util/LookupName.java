package org.motechproject.mds.util;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class for dealing with lookup names.
 */
public final class LookupName {

    public static String lookupMethod(String lookupName) {
        if (StringUtils.isBlank(lookupName)) {
            return lookupName;
        }
        // first split by space
        String[] words = StringUtils.split(lookupName, ' ');
        // then create camel case
        // first word starts with lower case
        StringBuilder sb = new StringBuilder(StringUtils.uncapitalize(words[0]));
        // rest with upper case
        for (int i = 1; i < words.length; i++) {
            sb.append(StringUtils.capitalize(words[i]));
        }

        return sb.toString();
    }

    public static String lookupCountMethod(String lookupNameOrMethodName) {
        return "count" + StringUtils.capitalize(lookupMethod(lookupNameOrMethodName));
    }

    private LookupName() {
    }
}
