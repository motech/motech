package org.motechproject.mds.query;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertNull;

public class CollectionPropertyTest extends PropertyTest {

    @Override
    protected Property getProperty() {
        return new CollectionProperty("roles", Arrays.asList("admin", "moderator"), String.class.getName());
    }

    @Override
    protected int getIdx() {
        return 5;
    }

    @Override
    protected String expectedFilter() {
        return "(roles.contains(param5_0) || roles.contains(param5_1))";
    }

    @Override
    protected String expectedDeclareParameter() {
        return "java.lang.String param5_0, java.lang.String param5_1";
    }

    @Override
    protected Collection expectedUnwrap() {
        return Arrays.asList("admin", "moderator");
    }

    @Test
    public void shouldIgnoreEmptyCollection() {
        CollectionProperty property = new CollectionProperty("name", Collections.emptyList(), String.class.getName());
        assertNull(property.asFilter(0));
        assertNull(property.asDeclareParameter(0));
    }
}
