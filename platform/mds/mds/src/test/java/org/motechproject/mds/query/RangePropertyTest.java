package org.motechproject.mds.query;

import org.motechproject.commons.api.Range;

import java.util.Arrays;
import java.util.Collection;

public class RangePropertyTest extends PropertyTest {

    @Override
    protected Property getProperty() {
        return new RangeProperty<>("gaussian", new Range<>(0, 1), Integer.class.getName());
    }

    @Override
    protected int getIdx() {
        return 3;
    }

    @Override
    protected String expectedFilter() {
        return "gaussian>=param3lb && gaussian<=param3ub";
    }

    @Override
    protected String expectedDeclareParameter() {
        return "java.lang.Integer param3lb, java.lang.Integer param3ub";
    }

    @Override
    protected Collection expectedUnwrap() {
        return Arrays.asList(0, 1);
    }
}
