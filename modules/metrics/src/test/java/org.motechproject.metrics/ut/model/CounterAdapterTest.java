package org.motechproject.metrics.ut.model;

import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.motechproject.metrics.model.CounterAdapter;

import static org.junit.Assert.assertEquals;

public class CounterAdapterTest {
    @Test
    public void testCounterWithMetricsEnabled() {
        CounterAdapter counterAdapter = new CounterAdapter(new com.codahale.metrics.Counter(), true);

        assertEquals(counterAdapter.getCount(), 0);

        counterAdapter.inc();
        assertEquals(counterAdapter.getCount(), 1);

        counterAdapter.inc(4);
        assertEquals(counterAdapter.getCount(), 5);

        counterAdapter.dec();
        assertEquals(counterAdapter.getCount(), 4);

        counterAdapter.dec(3);
        assertEquals(counterAdapter.getCount(), 1);
    }

    @Test
    public void testCounterWithMetricsDisabled() {
        CounterAdapter counterAdapter = new CounterAdapter(new com.codahale.metrics.Counter(), false);

        assertEquals(counterAdapter.getCount(), 0);

        counterAdapter.inc();
        assertEquals(counterAdapter.getCount(), 0);

        counterAdapter.inc(4);
        assertEquals(counterAdapter.getCount(), 0);

        counterAdapter.dec();
        assertEquals(counterAdapter.getCount(), 0);

        counterAdapter.dec(3);
        assertEquals(counterAdapter.getCount(), 0);
    }
}
