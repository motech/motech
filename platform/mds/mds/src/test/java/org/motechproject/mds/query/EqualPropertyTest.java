package org.motechproject.mds.query;

import java.util.Arrays;
import java.util.Collection;

public class EqualPropertyTest extends PropertyTest {

    @Override
    protected Property getProperty() {
        return new EqualProperty<>("id", 5L);
    }

    @Override
    protected int getIdx() {
        return 2;
    }

    @Override
    protected String expectedFilter() {
        return "id == param2";
    }

    @Override
    protected String expectedDeclareParameter() {
        return "java.lang.Long param2";
    }

    @Override
    protected Collection expectedUnwrap() {
        return Arrays.asList(5L);
    }
}
