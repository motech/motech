package org.motechproject.event.metrics;

import java.util.Map;


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
}
