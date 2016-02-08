package org.motechproject.metrics.api;

/**
 * Represents a metric that maintains a count.
 */
public interface Counting {
    /**
     * Get the current count.
     *
     * @return the current count
     */
    long getCount();
}
