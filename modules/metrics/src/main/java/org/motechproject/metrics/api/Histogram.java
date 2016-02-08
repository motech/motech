package org.motechproject.metrics.api;

/**
 * Represents a metric that measures a distribution of values from a stream of data that changes over time.
 */
public interface Histogram extends Metric, Counting, Sampling {
    /**
     * Update the histogram by recording a new value.
     *
     * @param value the value to record
     */
    void update(int value);

    /**
     * Update the histogram by recording a new value.
     *
     * @param value the value to record
     */
    void update(long value);
}
