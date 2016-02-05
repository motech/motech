package org.motechproject.metrics.model;

import org.motechproject.metrics.api.Meter;

/**
 * A meter implementation that can be enabled or disabled depending on configuration settings.
 */
public class MeterAdapter implements Meter, Enablable {
    private final com.codahale.metrics.Meter meter;
    private boolean isEnabled;

    public MeterAdapter(com.codahale.metrics.Meter meter, boolean enabled) {
        this.meter = meter;
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
     * Record an event.
     */
    @Override
    public void mark() {
        if (isEnabled()) {
            meter.mark();
        }
    }

    /**
     * Record the provided number of events.
     *
     * @param n the number of events
     */
    @Override
    public void mark(long n) {
        if (isEnabled()) {
            meter.mark(n);
        }
    }

    /**
     * Get the number of recorded events.
     *
     * @return the number of recorded events
     */
    @Override
    public long getCount() {
        return meter.getCount();
    }

    /**
     * Get the fifteen-minute weighted average of events since the meter's creation.
     *
     * @return the fifteen-minute weighted average of events since the meter's creation
     */
    @Override
    public double getFifteenMinuteRate() {
        return meter.getFifteenMinuteRate();
    }

    /**
     * Get the five-minute weighted average of events since the meter's creation.
     *
     * @return the five-minute weighted average of events since the meter's creation
     */
    @Override
    public double getFiveMinuteRate() {
        return meter.getFiveMinuteRate();
    }

    /**
     * Get the mean average of events since the meter's creation.
     *
     * @return the mean average of events since the meter's creation
     */
    @Override
    public double getMeanRate() {
        return meter.getMeanRate();
    }

    /**
     * Get the one-minute weighted average of events since the meter's creation.
     *
     * @return the one-minute weighted average of events since the meter's creation
     */
    @Override
    public double getOneMinuteRate() {
        return meter.getOneMinuteRate();
    }
}
