package org.motechproject.metrics.ut.model;

import org.junit.Test;
import org.motechproject.metrics.model.MeterAdapter;

import static org.junit.Assert.assertEquals;

public class MeterAdapterTest {
    @Test
    public void testMeterWithMetricsEnabled() throws InterruptedException {
        MeterAdapter meterAdapter = new MeterAdapter(new com.codahale.metrics.Meter(), true);

        assertEquals(0, meterAdapter.getCount());
        assertEquals(0, meterAdapter.getMeanRate(), 0);
        assertEquals(0, meterAdapter.getOneMinuteRate(), 0);
        assertEquals(0, meterAdapter.getFiveMinuteRate(), 0);
        assertEquals(0, meterAdapter.getFifteenMinuteRate(), 0);

        meterAdapter.mark();
        assertEquals(1, meterAdapter.getCount());

        meterAdapter.mark(4);
        assertEquals(5, meterAdapter.getCount());

        Thread.sleep(5000);

        assertEquals(1, meterAdapter.getOneMinuteRate(), .01);
        assertEquals(1, meterAdapter.getFiveMinuteRate(), .01);
        assertEquals(1, meterAdapter.getFifteenMinuteRate(), .01);
        assertEquals(1, meterAdapter.getMeanRate(), .01);
    }

    @Test
    public void testMeterWithMetricsDisabled() throws InterruptedException {
        MeterAdapter meterAdapter = new MeterAdapter(new com.codahale.metrics.Meter(), false);

        assertEquals(0, meterAdapter.getCount());
        assertEquals(0, meterAdapter.getMeanRate(), 0);
        assertEquals(0, meterAdapter.getOneMinuteRate(), 0);
        assertEquals(0, meterAdapter.getFiveMinuteRate(), 0);
        assertEquals(0, meterAdapter.getFifteenMinuteRate(), 0);

        meterAdapter.mark();
        assertEquals(0, meterAdapter.getCount());

        meterAdapter.mark(4);
        assertEquals(0, meterAdapter.getCount());

        Thread.sleep(5000);

        assertEquals(0, meterAdapter.getOneMinuteRate(), 0);
        assertEquals(0, meterAdapter.getFiveMinuteRate(), 0);
        assertEquals(0, meterAdapter.getFifteenMinuteRate(), 0);
        assertEquals(0, meterAdapter.getMeanRate(), 0);
    }
}
