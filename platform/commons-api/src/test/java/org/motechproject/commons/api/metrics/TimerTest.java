package org.motechproject.commons.api.metrics;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TimerTest {

    @Test
    public void testSeconds() throws InterruptedException {
        Timer timer = new Timer();
        Thread.sleep(1000);
        String s = timer.time();
        assertTrue(s.matches(".*"));
    }

    @Test
    public void testMilliseconds() throws InterruptedException {
        Timer timer = new Timer();
        Thread.sleep(10);
        String s = timer.time();
        assertTrue(s.matches(".*"));
    }
}
