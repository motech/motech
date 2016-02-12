package org.motechproject.metrics.model;

import org.motechproject.metrics.api.Histogram;
import org.motechproject.metrics.api.Snapshot;

/**
 * A histogram implementation that can be enabled or disabled depending on configuration settings.
 */
public class HistogramAdapter implements Histogram, Enablable {
    private final com.codahale.metrics.Histogram histogram;
    private boolean isEnabled;

    public HistogramAdapter(com.codahale.metrics.Histogram histogram, boolean enabled) {
        this.histogram = histogram;
        isEnabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    /**
     * If metrics are enabled, then add another value to the histogram.
     *
     * @param value the value to record
     */
    @Override
    public void update(int value) {
        if (isEnabled()) {
            histogram.update(value);
        }
    }

    /**
     * If metrics are enabled, then add another value to the histogram.
     *
     * @param value the value to record
     */
    @Override
    public void update(long value) {
        if (isEnabled()) {
            histogram.update(value);
        }
    }

    @Override
    public long getCount() {
        return histogram.getCount();
    }

    @Override
    public Snapshot getSnapshot() {
        return new SnapshotAdapter(histogram.getSnapshot());
    }
}
