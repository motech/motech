package org.motechproject.valueobjects.factory;

import org.junit.Test;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import static junit.framework.Assert.assertEquals;

public class WallTimeFactoryTest {

    @Test
    public void create() {
        assertEquals(new WallTime(1, WallTimeUnit.Day), WallTimeFactory.wallTime("1 Day"));
        assertEquals(new WallTime(1, WallTimeUnit.Day), WallTimeFactory.wallTime("1 day"));
        assertEquals(new WallTime(1, WallTimeUnit.Day), WallTimeFactory.wallTime(" 1 day "));
        assertEquals(new WallTime(2, WallTimeUnit.Day), WallTimeFactory.wallTime(" 2 days "));
        assertEquals(new WallTime(2, WallTimeUnit.Week), WallTimeFactory.wallTime(" 2 weeks "));
    }

    @Test
    public void shouldReturnNullForEmptyValues() {
        assertEquals(null, WallTimeFactory.wallTime(""));
    }
}
