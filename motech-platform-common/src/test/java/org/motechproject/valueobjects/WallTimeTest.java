package org.motechproject.valueobjects;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class WallTimeTest {

    @Test
    public void periodElapsedSince() {
        assertEquals(3, new WallTime(3, WallTimeUnit.Day).inDays());
        assertEquals(7, new WallTime(1, WallTimeUnit.Week).inDays());
        assertEquals(0, new WallTime(0, WallTimeUnit.Week).inDays());
        assertEquals(0, new WallTime(0, null).inDays());
    }

    @Test
    public void zeroValue() {
        assertEquals(0, new WallTime(0, null).asPeriod().getDays());
    }
}
