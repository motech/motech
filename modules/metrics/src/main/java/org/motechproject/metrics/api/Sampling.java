package org.motechproject.metrics.api;

/**
 * Represents an object that collects samples of values.
 */
public interface Sampling {
    /**
     * Returns a snapshot of the collected values.
     *
     * @return the snapshot of collected values.
     */
    Snapshot getSnapshot();
}
