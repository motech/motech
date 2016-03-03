package org.motechproject.mds.util;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class for dealing with lookup names.
 */
public final class LookupName {

    /**
     * Builds lookup method name, based on the lookup name. The resulting method name is
     * camelCase, based on the words in the lookup.
     *
     * @param lookupName name of the lookup
     * @return camelCase lookup method name
     */
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

    /**
     * Builds count lookup name. The resulting method name will be in form: <code>countXxxYyyZzz</code>.
     *
     * @param lookupNameOrMethodName name of the lookup or lookup method name
     * @return count lookup method name
     */
    public static String lookupCountMethod(String lookupNameOrMethodName) {
        return "count" + StringUtils.capitalize(lookupMethod(lookupNameOrMethodName));
    }

    /**
     * Builds lookup field name which may contain information about the searching by relationship.
     *
     * @param fieldName The name of the field from entity
     * @param relatedFieldName The name of the field in related entity
     * @return lookup field name
     */
    public static String buildLookupFieldName(String fieldName, String relatedFieldName) {
        if (StringUtils.isNotBlank(relatedFieldName)) {
            return String.format("%s.%s", fieldName, relatedFieldName);
        }
        return fieldName;
    }

    /**
     * Returns a field name in related entity.
     *
     * @param lookupFieldName the lookup field name
     * @return field name from related entity
     */
    public static String getRelatedFieldName(String lookupFieldName) {
        if (lookupFieldName.contains(".")) {
            return lookupFieldName.split("\\.")[1];
        }
        return null;
    }

    /**
     * Returns the name of the field from entity.
     *
     * @param lookupFieldName the lookup field name
     * @return field name from entity
     */
    public static String getFieldName(String lookupFieldName) {
        if (lookupFieldName.contains(".")) {
            return lookupFieldName.split("\\.")[0];
        }
        return lookupFieldName;
    }

    private LookupName() {
    }
}
