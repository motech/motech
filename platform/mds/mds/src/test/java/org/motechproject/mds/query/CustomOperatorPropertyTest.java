package org.motechproject.mds.query;

import org.junit.Test;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class CustomOperatorPropertyTest extends PropertyTest {

    @Override
    protected boolean ignoresNull() {
        return false;
    }

    @Override
    protected Property getProperty() {
        return new CustomOperatorProperty<>("length", 5, Integer.class.getName(), "<");
    }

    @Override
    protected int getIdx() {
        return 3;
    }

    @Override
    protected String expectedFilter() {
        return "length < param3";
    }

    @Override
    protected String expectedDeclareParameter() {
        return "java.lang.Integer param3";
    }

    @Override
    protected Collection expectedUnwrap() {
        return asList(5);
    }

    @Test
    public void shouldCreateMethodFilter() {
        CustomOperatorProperty<String> property = new CustomOperatorProperty<>("subject", "test", String.class.getName(),
                "matches()");
        assertEquals("subject.matches(param0)", property.asFilter(0));
        assertEquals("java.lang.String param0", property.asDeclareParameter(0));
        assertEquals(asList("test"), property.unwrap());
    }
}
