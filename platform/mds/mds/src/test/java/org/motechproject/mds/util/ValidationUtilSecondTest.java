package org.motechproject.mds.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.motechproject.mds.exception.entity.InvalidJavaFieldNameException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(value = Parameterized.class)
public class ValidationUtilSecondTest {

    private final String identifier;

    public ValidationUtilSecondTest(String identifier) {
        this.identifier = identifier;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        List<String> identifiers = Arrays.asList("123", "123abc", "abc*", "*abc");

        Collection<Object[]> params = new ArrayList<>();
        for (String identifier : identifiers) {
            params.add(new Object[]{ identifier });
        }

        return params;
    }

    @Test(expected = InvalidJavaFieldNameException.class)
    public void shouldRecognizeInvalidJavaIdentifiers() {
        ValidationUtil.validateValidJavaFieldName(identifier);

    }
}
