package org.motechproject.mds.util;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.exception.entity.InvalidJavaFieldNameException;
import org.motechproject.mds.exception.entity.ReservedKeywordException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Common validation utils for mds.
 */
public final class ValidationUtil {

    private static final Set<String> JAVA_KEYWORDS;

    static {
        Set<String> set = new HashSet<>();
        set.addAll(Arrays.asList("abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
                "const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
                "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new",
                "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch",
                "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while",
                //true, false and null are literals not keywords, but you still cannot use them as identifiers
                "true", "false", "null"));

        JAVA_KEYWORDS = Collections.unmodifiableSet(set);
    }

    /**
     * Verifies that given string is not a reserved Java keyword. Throws {@link org.motechproject.mds.exception.entity.ReservedKeywordException}
     * if given String is reserved.
     *
     * @param str String to verify
     */
    public static void validateNoJavaKeyword(String str) {
        if (JAVA_KEYWORDS.contains(str)) {
            throw new ReservedKeywordException(str);
        }
    }

    /**
     * Verifies that given string is a valid java field name. Throws {@link org.motechproject.mds.exception.entity.InvalidJavaFieldNameException}
     * if given String is not blank and is not valid java identifier.
     *
     * @param str String to verify
     */
    public static void validateValidJavaFieldName(String str) {
        if (!StringUtils.isBlank(str)) {
            if (!Character.isJavaIdentifierStart(str.charAt(0))) {
                throw new InvalidJavaFieldNameException(str);
            }

            for (char s : str.toCharArray()) {
                if(!Character.isJavaIdentifierPart(s)) {
                    throw new InvalidJavaFieldNameException(str);
                }
            }
        }
    }

    private ValidationUtil() {
    }
}
