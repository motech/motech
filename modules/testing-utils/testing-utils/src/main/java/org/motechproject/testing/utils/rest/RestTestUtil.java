package org.motechproject.testing.utils.rest;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.motechproject.commons.api.MotechException;

import java.io.IOException;

/**
 * A utility for unit testing REST controllers.
 */
public final class RestTestUtil {

    private RestTestUtil() {
        // static utility class
    }

    /**
     * Returns a JSON matcher that will match the given response against the expected one.
     * The json files will be compared without respecting the order in which the variables appear in.
     * This matcher was written with Spring MVC Testing in mind, but can be potentially used in other cases.
     * @param expected the expected JSON output, as string
     * @return an instance of a matcher that will perform the match
     * @throws MotechException if we failed to parse the expected or actual string values into JSON
     */
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
