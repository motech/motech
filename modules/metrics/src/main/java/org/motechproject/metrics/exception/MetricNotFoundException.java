package org.motechproject.metrics.exception;

/**
 * A custom exception thrown when a metric does not exist by name in the metric registry, and the default behavior
 * of creating a new metric of the appropriate type and name is not an appropriate solution.
 */
public class MetricNotFoundException extends RuntimeException {

    public MetricNotFoundException(String message) {
        super(message);
    }

    public MetricNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
