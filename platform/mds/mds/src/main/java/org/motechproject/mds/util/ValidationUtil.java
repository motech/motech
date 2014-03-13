package org.motechproject.mds.util;

import org.motechproject.mds.ex.ReservedKeywordException;

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
        set.addAll(Arrays.asList("abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
                "class", "const", "continue", "default", "double", "else", "enum", "extends", "final", "finally",
                "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long",
                "native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
                "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile",
                "while"));

        JAVA_KEYWORDS = Collections.unmodifiableSet(set);
    }

    public static void validateNoJavaKeyword(String str) {
        if (JAVA_KEYWORDS.contains(str)) {
            throw new ReservedKeywordException(str);
        }
    }

    private ValidationUtil() {
    }
}
