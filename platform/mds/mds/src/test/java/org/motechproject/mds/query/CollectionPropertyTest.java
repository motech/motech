package org.motechproject.mds.query;

import java.util.Arrays;
import java.util.Collection;

public class CollectionPropertyTest extends PropertyTest {

    @Override
    protected Property getProperty() {
        return new CollectionProperty("roles", Arrays.asList("admin", "moderator"));
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
}
