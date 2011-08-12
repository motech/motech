/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.metrics.impl;

import org.motechproject.metrics.MetricsAgent;
import org.motechproject.metrics.MetricsAgentBackend;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipleMetricsAgentImpl implements MetricsAgent
{
    List<MetricsAgentBackend> metricsAgents;

    Map<String, Long> timers;

    public MultipleMetricsAgentImpl() {
        timers = new HashMap<String, Long>();
    }

    /**
     * Reports an occurrence of metric, incrementing it's count.  Not all implementations
     * may make use of parameters
     *
     * @param metric     The metric being recorded
     * @param parameters Optional parameters related to the event
     */
    @Override
    public void logEvent(String metric, Map<String, String> parameters)
    {
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
    public void logEvent(String metric)
    {
        for (MetricsAgentBackend agent : getMetricsAgents()) {
            agent.logEvent(metric);
        }
    }

    /**
     * Starts a timer for metric.  Later calls to startTimer without a corresponding call to endTimer for the same
     * metric are ignored
     *
     * @param metric The metric being timed
     */
    @Override
    public void startTimer(String metric)
    {
        if (!timers.containsKey(metric)) {
            timers.put(metric, DateUtil.now().getMillis());
        }
    }

    /**
     * Ends the timer for metric and records it.  No action is taken if a start timer was not recorded for metric
     *
     * @param metric The metric being timed
     */
    @Override
    public void stopTimer(String metric)
    {
        if (timers.containsKey(metric)) {
            long startTime = timers.get(metric);
            long endTime = DateUtil.now().getMillis();
            long executionTime = endTime - startTime;

            for (MetricsAgentBackend agent : getMetricsAgents()) {
                agent.logTimedEvent(metric, executionTime);
            }

            timers.remove(metric);
        }
    }

    public void addMetricAgent(MetricsAgentBackend agent)
    {
        if (metricsAgents == null) {
            metricsAgents = new ArrayList<MetricsAgentBackend>();
        }

        metricsAgents.add(agent);
    }

    public List<MetricsAgentBackend> getMetricsAgents()
    {
        if (metricsAgents == null) {
            return new ArrayList<MetricsAgentBackend>();
        }

        return metricsAgents;
    }

    public void setMetricsAgents(List<MetricsAgentBackend> agents) {
        metricsAgents = agents;
    }
}
