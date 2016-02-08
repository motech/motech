package org.motechproject.metrics.model;

/**
 * Marks metrics that are able to be turned on or off.
 */
public interface Enablable {
    /**
     * Returns whether or not the metric is currently enabled.
     *
     * @return true if enabled, false otherwise
     */
    boolean isEnabled();

    /**
     * Sets whether or not the metric is enabled according to the enabled parameter.
     *
     * @param enabled true to enable, false to disable
     */
    void setEnabled(boolean enabled);
}
