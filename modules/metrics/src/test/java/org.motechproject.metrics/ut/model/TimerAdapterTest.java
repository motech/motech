package org.motechproject.metrics.ut.model;

import org.junit.Test;
import org.motechproject.metrics.model.TimerAdapter;
import org.motechproject.metrics.api.Timer.Context;

import static org.junit.Assert.assertEquals;

public class TimerAdapterTest {
    @Test
    public void testTimerWithMetricsEnabled() throws InterruptedException {
        TimerAdapter timerAdapter = new TimerAdapter(new com.codahale.metrics.Timer(), true);

        Context context = timerAdapter.time();
        Thread.sleep(1000);
        context.stop();

        Thread.sleep(4000);

        assertEquals(1, timerAdapter.getCount());

        assertEquals(1, timerAdapter.getCount(), 0);
        assertEquals(0.2, timerAdapter.getOneMinuteRate(), .01);
        assertEquals(0.2, timerAdapter.getFiveMinuteRate(), .01);
        assertEquals(0.2, timerAdapter.getFifteenMinuteRate(), .01);
        assertEquals(0.2, timerAdapter.getMeanRate(), .01);
    }

    @Test
    public void testTimerWithMetricsDisabled() throws InterruptedException {
        TimerAdapter timerAdapter = new TimerAdapter(new com.codahale.metrics.Timer(), false);

        Context context = timerAdapter.time();
        Thread.sleep(1000);
        context.stop();

        assertEquals(0, timerAdapter.getCount(), 0);
        assertEquals(0, timerAdapter.getOneMinuteRate(), 0);
        assertEquals(0, timerAdapter.getFiveMinuteRate(), 0);
        assertEquals(0, timerAdapter.getFifteenMinuteRate(), 0);
        assertEquals(0, timerAdapter.getMeanRate(), 0);
    }

}
