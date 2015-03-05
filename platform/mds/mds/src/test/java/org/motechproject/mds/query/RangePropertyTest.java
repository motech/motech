package org.motechproject.mds.query;

import org.junit.Test;
import org.motechproject.commons.api.Range;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void shouldIgnoreNullBounds() {
        RangeProperty<Integer> rangeProperty =
                new RangeProperty<>("rangeProp", new Range<>(null, 10), Integer.class.getName());

        assertEquals("rangeProp<=param0ub", rangeProperty.asFilter(0));
        assertEquals("java.lang.Integer param0ub", rangeProperty.asDeclareParameter(0));
        assertEquals(Arrays.asList(10), rangeProperty.unwrap());

        rangeProperty = new RangeProperty<>("rangeProp", new Range<>(10, null), Integer.class.getName());

        assertEquals("rangeProp>=param0lb", rangeProperty.asFilter(0));
        assertEquals("java.lang.Integer param0lb", rangeProperty.asDeclareParameter(0));
        assertEquals(Arrays.asList(10), rangeProperty.unwrap());
    }
}
