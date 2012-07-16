package org.motechproject.metrics.impl;

import org.motechproject.metrics.MetricsAgent;
import org.motechproject.metrics.MetricsAgentBackend;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultipleMetricsAgentImpl implements MetricsAgent {
    @Autowired
    List<MetricsAgentBackend> metricsAgents;


    public MultipleMetricsAgentImpl() {
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
        for (MetricsAgentBackend agent : getMetricsAgents()) {
            agent.logEvent(metric, parameters);
        }
    }

    /**
     * Reports an occurrence of metric, incrementing it's count.
     *
     * @param metric The metric being recorded
     */
    @Override
    public void logEvent(String metric) {
        for (MetricsAgentBackend agent : getMetricsAgents()) {
            agent.logEvent(metric);
        }
    }

    /**
     * Starts a timer for metric.  Later calls to startTimer without a corresponding call to endTimer for the same
     * metric are ignored
     */
    @Override
    public long startTimer() {
        return DateUtil.now().getMillis();
    }

    /**
     * Ends the timer for metric and records it.  No action is taken if a start timer was not recorded for metric
     *
     * @param metric     The metric being timed
     * @param startTime
     */
    @Override
    public void stopTimer(String metric, long startTime) {
        long endTime = DateUtil.now().getMillis();
        long executionTime = endTime - startTime;

        for (MetricsAgentBackend agent : getMetricsAgents()) {
            agent.logTimedEvent(metric, executionTime);
        }
    }

    public void addMetricAgent(MetricsAgentBackend agent) {
        if (metricsAgents == null) {
            metricsAgents = new ArrayList<MetricsAgentBackend>();
        }

        metricsAgents.add(agent);
    }

    public List<MetricsAgentBackend> getMetricsAgents() {
        if (metricsAgents == null) {
            return new ArrayList<MetricsAgentBackend>();
        }

        return metricsAgents;
    }

    public void setMetricsAgents(List<MetricsAgentBackend> agents) {
        metricsAgents = agents;
    }
}
