package org.motechproject.metrics.api;

/**
 * Represents a metric that increments and decrements a counted value.
 */
public interface Counter extends Metric, Counting {
    /**
     * Increment the counter's current value by one.
     */
    void inc();

    /**
     * Increment the counter's current value by the provided value.
     *
     * @param n the value to increment
     */
    void inc(long n);

    /**
     * Decrement the counter's current value by one.
     */
    void dec();

    /**
     * Decrement the counter's current value by the provided value.
     *
     * @param n the value to decrement
     */
    void dec(long n);
}
