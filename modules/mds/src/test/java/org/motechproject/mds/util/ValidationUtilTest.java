package org.motechproject.mds.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.motechproject.mds.ex.ReservedKeywordException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(value = Parameterized.class)
public class ValidationUtilTest {

    private final String keywordParam;

    public ValidationUtilTest(String keywordParam) {
        this.keywordParam = keywordParam;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        List<String> keywords = Arrays.asList("abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
                "class", "const", "continue", "default", "double", "else", "enum", "extends", "final", "finally",
                "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface",
                "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static",
                "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void",
                "volatile", "while");

        Collection<Object[]> params = new ArrayList<>();
        for (String keyword : keywords) {
            params.add(new Object[]{ keyword });
        }

        return params;
    }

    @Test(expected = ReservedKeywordException.class)
    public void shouldRecognizeJavaKeywords() {
        ValidationUtil.validateNoJavaKeyword(keywordParam);
    }
}
