package org.motechproject.metrics.api;

/**
 * Represents a metric that measures throughput.
 */
public interface Meter extends Metric, Metered {
    /**
     * Mark the occurence of an event.
     */
    void mark();

    /**
     * Mark the occurence of a number of events.
     *
     * @param n the number of events
     */
    void mark(long n);
}
