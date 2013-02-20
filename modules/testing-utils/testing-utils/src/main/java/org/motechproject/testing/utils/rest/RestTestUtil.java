package org.motechproject.testing.utils.rest;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.motechproject.commons.api.MotechException;

import java.io.IOException;

public final class RestTestUtil {

    private RestTestUtil() {
        // static utility class
    }

    public static Matcher<String> jsonMatcher(final String expected) {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(Object argument) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();

                    String actual = (String) argument;

                    JsonNode expectedTree = objectMapper.readTree(expected);
                    JsonNode actualTree = objectMapper.readTree(actual);

                    return expectedTree.equals(actualTree);
                } catch (IOException e) {
                    throw new MotechException("Json parsing failure", e);
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(expected);
            }
        };
    }
}
