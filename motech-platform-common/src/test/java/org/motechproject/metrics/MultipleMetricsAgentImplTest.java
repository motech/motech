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
package org.motechproject.metrics;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.motechproject.metrics.impl.MultipleMetricsAgentImpl;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * MultipleMetricsAgentImpl Tester.
 *
 * @author <Authors name>
 * @since <pre>03/25/2011</pre>
 * @version 1.0
 */
public class MultipleMetricsAgentImplTest {

    @Test
    public void testLogEventNullParameters() throws Exception {
        MultipleMetricsAgentImpl metricsAgent = new MultipleMetricsAgentImpl();
        MetricsAgentBackend agent = mock(MetricsAgentBackend.class);
        metricsAgent.addMetricAgent(agent);

        metricsAgent.logEvent("test.metric", null);

        verify(agent).logEvent("test.metric", null);
    }

    @Test
    public void testLogEvent() throws Exception {
        MultipleMetricsAgentImpl metricsAgent = new MultipleMetricsAgentImpl();
        MetricsAgentBackend agent = mock(MetricsAgentBackend.class);
        metricsAgent.addMetricAgent(agent);

        metricsAgent.logEvent("test.metric");

        verify(agent).logEvent("test.metric");
    }

    @Test
    public void testLogEventNoAgents() throws Exception {
        MultipleMetricsAgentImpl metricsAgent = new MultipleMetricsAgentImpl();

        metricsAgent.logEvent("test.metric", null);
    }

    @Test
    public void testLogEventTwoAgents() {
        MultipleMetricsAgentImpl metricsAgent = new MultipleMetricsAgentImpl();

        MetricsAgentBackend agent1 = mock(MetricsAgentBackend.class);
        metricsAgent.addMetricAgent(agent1);

        MetricsAgentBackend agent2 = mock(MetricsAgentBackend.class);
        metricsAgent.addMetricAgent(agent2);


        metricsAgent.logEvent("test.metric");

        verify(agent1).logEvent("test.metric");
        verify(agent2).logEvent("test.metric");
    }

    @Test
    public void testMultipleStartTimerCalls() throws InterruptedException
    {
        MultipleMetricsAgentImpl metricsAgent = new MultipleMetricsAgentImpl();

        MetricsAgentBackend agent = mock(MetricsAgentBackend.class);
        ArgumentCaptor<Long> argument = ArgumentCaptor.forClass(Long.class);
        metricsAgent.addMetricAgent(agent);

        metricsAgent.startTimer("test.metric");
        sleep(1000);
        metricsAgent.startTimer("test.metric");
        metricsAgent.stopTimer("test.metric");

        verify(agent).logTimedEvent(anyString(), argument.capture());
        assertTrue(argument.getValue().longValue() >= 1000);
    }

    @Test
    public void testStartTimerNoAgents() {
        MultipleMetricsAgentImpl metricsAgent = new MultipleMetricsAgentImpl();

        metricsAgent.startTimer("test.metric");
    }

    @Test
    public void testTimerOneAgent() {
        MultipleMetricsAgentImpl metricsAgent = new MultipleMetricsAgentImpl();

        MetricsAgentBackend agent1 = mock(MetricsAgentBackend.class);
        metricsAgent.addMetricAgent(agent1);

        metricsAgent.startTimer("test.metric");
        metricsAgent.stopTimer("test.metric");

        verify(agent1).logTimedEvent(anyString(), anyLong());
    }

    @Test
    public void testTimerTwoAgents() {
        MultipleMetricsAgentImpl metricsAgent = new MultipleMetricsAgentImpl();

        MetricsAgentBackend agent1 = mock(MetricsAgentBackend.class);
        metricsAgent.addMetricAgent(agent1);

        MetricsAgentBackend agent2 = mock(MetricsAgentBackend.class);
        metricsAgent.addMetricAgent(agent2);

        metricsAgent.startTimer("test.metric");
        metricsAgent.stopTimer("test.metric");

        verify(agent1).logTimedEvent(anyString(), anyLong());
        verify(agent2).logTimedEvent(anyString(), anyLong());
    }


    @Test
    public void testDoubleStopTimerCall() {
        MultipleMetricsAgentImpl metricsAgent = new MultipleMetricsAgentImpl();

        MetricsAgentBackend agent = mock(MetricsAgentBackend.class);
        metricsAgent.addMetricAgent(agent);

        metricsAgent.startTimer("test.metric");
        metricsAgent.stopTimer("test.metric");
        metricsAgent.stopTimer("test.metric");

        verify(agent, times(1)).logTimedEvent(anyString(), anyLong());
    }
}
