package org.motechproject.model;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class TimeTest {
    @Test
    public void parseTime() {
        assertEquals(new Time(10, 12), Time.parseTime("10:12", ":"));
    }
}
