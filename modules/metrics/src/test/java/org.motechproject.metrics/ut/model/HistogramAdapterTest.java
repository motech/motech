package org.motechproject.metrics.ut.model;

import com.codahale.metrics.ExponentiallyDecayingReservoir;
import org.junit.Test;
import org.motechproject.metrics.api.Snapshot;
import org.motechproject.metrics.model.HistogramAdapter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class HistogramAdapterTest {
    @Test
    public void testHistogramWithMetricsEnabled() {
        HistogramAdapter histogramAdapter = new HistogramAdapter(new com.codahale.metrics.Histogram(new ExponentiallyDecayingReservoir()), true);

        for (int i = 1; i <= 9; i++) {
            histogramAdapter.update(i);
        }

        assertEquals(9, histogramAdapter.getCount());

        histogramAdapter.update(10L);

        assertEquals(10, histogramAdapter.getCount());

        Snapshot snapshot = histogramAdapter.getSnapshot();
        assertEquals(8.0, snapshot.get75thPercentile(), 0);
        assertEquals(10, snapshot.get95thPercentile(), 0);
        assertEquals(10, snapshot.get99thPercentile(), 0);
        assertEquals(10, snapshot.get999thPercentile(), 0);
        assertEquals(10, snapshot.getMax());
        assertEquals(5.5, snapshot.getMean(), .001);
        assertEquals(6.0, snapshot.getMedian(), 0);
        assertEquals(1, snapshot.getMin());
        assertEquals(2.872, snapshot.getStdDev(), .001);
        assertEquals(6.0, snapshot.getValue(.5), 0);
        assertArrayEquals(new long[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, snapshot.getValues());
    }

    @Test
    public void testHistogramWithMetricsDisabled() {
        HistogramAdapter histogramAdapter = new HistogramAdapter(new com.codahale.metrics.Histogram(new ExponentiallyDecayingReservoir()), false);

        for (int i = 1; i <= 9; i++) {
            histogramAdapter.update(i);
        }

        assertEquals(0, histogramAdapter.getCount());

        histogramAdapter.update(10L);

        assertEquals(0, histogramAdapter.getCount());

        Snapshot snapshot = histogramAdapter.getSnapshot();
        assertEquals(0, snapshot.get75thPercentile(), 0);
        assertEquals(0, snapshot.get95thPercentile(), 0);
        assertEquals(0, snapshot.get99thPercentile(), 0);
        assertEquals(0, snapshot.get999thPercentile(), 0);
        assertEquals(0, snapshot.getMax());
        assertEquals(0, snapshot.getMean(), 0);
        assertEquals(0, snapshot.getMedian(), 0);
        assertEquals(0, snapshot.getMin());
        assertEquals(0, snapshot.getStdDev(), 0);
        assertEquals(0, snapshot.getValue(.5), 0);
        assertArrayEquals(new long[]{}, snapshot.getValues());
    }
}
