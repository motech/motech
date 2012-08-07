package org.motechproject.metrics.impl;

import org.motechproject.metrics.MetricsAgentBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * A very simple metric backend that logs all metrics in a format splunk can parse (i.e key=value)
 */
public class LoggingAgentBackendImpl implements MetricsAgentBackend {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Logger metrics = LoggerFactory.getLogger("metrics");

    // Preset the prefix to limit the 'appends' I do later
    private String prefix;

    public LoggingAgentBackendImpl() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String hostName = addr.getCanonicalHostName();
            String ip = addr.getHostAddress();

            prefix = String.format("host=%s ip=%s ", hostName, ip);
        } catch (UnknownHostException e) {
            // bummer, but not very important.  Log an error and set them to null and we just won't log them later
            log.info(String.format("Unable to get host information: %s", e.getMessage()));
            prefix = "";
        }
    }


    /**
     * Reports an occurrence of metric, incrementing it's count.  Not all implementations
     * may make use of parameters
     *
     * @param metric     The metric being recorded
     * @param parameters Optional parameters related to the event
     */
    @Override
    public void logEvent(String metric, Map<String, String> parameters) {
        StringBuilder sb = new StringBuilder(prefix);
        sb.append(String.format("metric=%s", metric));

        if (parameters != null) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                sb.append(String.format(" %s=%s", entry.getKey(), entry.getValue()));
            }
        }

        metrics.info(sb.toString());
    }

    /**
     * Reports an occurrence of metric, incrementing it's count.
     *
     * @param metric The metric being recorded
     */
    @Override
    public void logEvent(String metric) {
        logEvent(metric, null);
    }

    /**
     * Reports an occurance of metric in milliseconds
     *
     * @param metric The metric being recorded
     * @param time   The execution time of this event in milliseconds
     */
    @Override
    public void logTimedEvent(String metric, long time) {
        metrics.info(String.format("%smetric=%s time=%d", prefix, metric, time));
    }
}
