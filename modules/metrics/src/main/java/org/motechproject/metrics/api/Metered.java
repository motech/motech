package org.motechproject.metrics.api;

/**
 * Represents a metric that maintains mean and exponentially-weighted rates.
 */
public interface Metered extends Counting {
    /**
     * Returns the fifteen-minute weighted moving average of events occurring since the meter's creation.
     *
     * @return the fifteen-minute weighted moving average.
     */
    double getFifteenMinuteRate();

    /**
     * Returns the five-minute weighted moving average of events occurring since the meter's creation.
     *
     * @return the five-minute weighted moving average.
     */
    double getFiveMinuteRate();

    /**
     * Returns the mean rate of events occurring since the meter's creation.
     *
     * @return the mean rate of events.
     */
    double getMeanRate();

    /**
     * Returns the one-minute weighted moving average of events occuring since the meter's creation.
     *
     * @return the one-minute weighted moving average.
     */
    double getOneMinuteRate();
}
