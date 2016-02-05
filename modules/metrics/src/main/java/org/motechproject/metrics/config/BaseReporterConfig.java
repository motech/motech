package org.motechproject.metrics.config;

import java.util.concurrent.TimeUnit;

/**
 * Represents configuration that is common across reporters.
 */
public abstract class BaseReporterConfig {
    /**
     * Whether or not the reporter is currently enabled.
     */
    private boolean enabled;

    /**
     * The time unit to convert metrics' rates into when reporting.
     */
    private TimeUnit convertRates;

    /**
     * The time unit to convert metrics' durations into when reporting.
     */
    private TimeUnit convertDurations;

    /**
     * The interval at which reports are streamed.
     */
    private int frequency;

    /**
     * The time unit associated with the frequency value at which reports are streamed.
     */
    private TimeUnit frequencyUnit;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public TimeUnit getConvertRates() {
        return convertRates;
    }

    public void setConvertRates(TimeUnit convertRates) {
        this.convertRates = convertRates;
    }

    public TimeUnit getConvertDurations() {
        return convertDurations;
    }

    public void setConvertDurations(TimeUnit convertDurations) {
        this.convertDurations = convertDurations;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public TimeUnit getFrequencyUnit() {
        return frequencyUnit;
    }

    public void setFrequencyUnit(TimeUnit frequencyUnit) {
        this.frequencyUnit = frequencyUnit;
    }
}
