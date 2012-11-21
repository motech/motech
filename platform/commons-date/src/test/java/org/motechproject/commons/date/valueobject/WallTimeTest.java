package org.motechproject.commons.date.valueobject;

import org.junit.Test;
import org.motechproject.commons.date.valueobjects.WallTime;

import static junit.framework.Assert.assertEquals;

public class WallTimeTest {

    @Test
    public void periodElapsedSince() {
        assertEquals(3, new WallTime("3 Day").inDays());
        assertEquals(7, new WallTime("1 Week").inDays());
        assertEquals(0, new WallTime("0 Week").inDays());
    }

    @Test
    public void zeroValue() {
        assertEquals(0, new WallTime("0 days").asPeriod().getDays());
    }
}
