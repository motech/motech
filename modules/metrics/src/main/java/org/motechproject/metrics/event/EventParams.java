package org.motechproject.metrics.event;

/**
 * The possible payload fields on Metrics module events.
 */
public final class EventParams {
    /**
     * The name of a metric to create or load from the metric registry.
     */
    public static final String METRIC_NAME = "metric_name";

    /**
     * The value by which to modify a metric created or loaded by name from the metric registry.
     */
    public static final String METRIC_VALUE = "metric_value";

    private EventParams() {}
}
