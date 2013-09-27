package org.motechproject.metrics;

import org.motechproject.metrics.domain.ConfigProperty;

import java.util.Map;

/**
 * A simple interface for metrics allowing event logging
 */

public interface MetricsAgentBackend {
    /**
     * Reports an occurrence of metric, incrementing it's count.  Not all implementations
     * may make use of parameters
     *
     * @param metric     The metric being recorded
     * @param parameters Optional parameters related to the event
     */
    void logEvent(String metric, Map<String, String> parameters);

    /**
     * Reports an occurrence of metric, incrementing it's count.
     *
     * @param metric The metric being recorded
     */
    void logEvent(String metric);

    /**
     * Reports an occurance of metric in milliseconds
     *
     * @param metric The metric being recorded
     * @param time   The execution time of this event in milliseconds
     */
    void logTimedEvent(String metric, long time);

    String getImplementationName();

    /**
     * Should return config properties of current implementation.
     * If there are no settings, then method should return empty map
     */
    Map<String, ConfigProperty> getSettings();

    /**
     * Should save new config
     */
    void saveSettings(Map<String, ConfigProperty> config);
}
