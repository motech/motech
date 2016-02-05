package org.motechproject.metrics.event;

/**
 * Subjects of events to which the Metrics module responds.
 */
public final class EventSubjects {
    /**
     * Initiates creating or loading a counter and incrementing by some value.
     */
    public static final String INCREMENT_COUNTER = "metrics_counter_increment";

    /**
     * Initiates creating or loading a counter and decrementing by some value.
     */
    public static final String DECREMENT_COUNTER = "metrics_counter_decrement";

    /**
     * Initiates creating or loading a meter and recording some value.
     */
    public static final String MARK_METER = "metrics_meter_mark";

    /**
     * Initiates creating or loading a histogram and recording some value.
     */
    public static final String UPDATE_HISTOGRAM = "metrics_histogram_update";

    private EventSubjects() {}
}
