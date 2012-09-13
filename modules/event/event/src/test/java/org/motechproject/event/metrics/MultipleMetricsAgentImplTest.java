package org.motechproject.event.metrics;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.motechproject.event.metrics.impl.MultipleMetricsAgentImpl;
import org.motechproject.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class MultipleMetricsAgentImplTest {
    @Test
    public void testLogEventNullParameters() {
        MultipleMetricsAgentImpl metricsAgent = new MultipleMetricsAgentImpl();
        MetricsAgentBackend agent = mock(MetricsAgentBackend.class);
        metricsAgent.addMetricAgent(agent);

        metricsAgent.logEvent("test.metric", null);

        verify(agent).logEvent("test.metric", null);
    }

    @Test
    public void testLogEvent() {
        MultipleMetricsAgentImpl metricsAgent = new MultipleMetricsAgentImpl();
        MetricsAgentBackend agent = mock(MetricsAgentBackend.class);
        metricsAgent.addMetricAgent(agent);

        metricsAgent.logEvent("test.metric");

        verify(agent).logEvent("test.metric");
    }

    @Test
    public void testLogEventNoAgents() {
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
    public void testMultipleStartTimerCalls() throws InterruptedException {
        PowerMockito.mockStatic(DateUtil.class);
        MultipleMetricsAgentImpl metricsAgent = new MultipleMetricsAgentImpl();

        MetricsAgentBackend agent = mock(MetricsAgentBackend.class);
        ArgumentCaptor<Long> argument = ArgumentCaptor.forClass(Long.class);
        metricsAgent.addMetricAgent(agent);

        DateTime date1 = new DateTime(2011, 1, 1, 10, 25, 30, 0);
        DateTime date2 = new DateTime(2011, 1, 1, 10, 25, 31, 0);
        when(DateUtil.now()).thenReturn(date1);
        long startTime = metricsAgent.startTimer();
        when(DateUtil.now()).thenReturn(date2);
        metricsAgent.stopTimer("test.metric", startTime);

        verify(agent).logTimedEvent(anyString(), argument.capture());
        assertEquals(1000, argument.getValue().longValue());
    }

    @Test
    public void testStartTimerNoAgents() {
        MultipleMetricsAgentImpl metricsAgent = new MultipleMetricsAgentImpl();

        metricsAgent.startTimer();
    }

    @Test
    public void testTimerOneAgent() {
        MultipleMetricsAgentImpl metricsAgent = new MultipleMetricsAgentImpl();

        MetricsAgentBackend agent1 = mock(MetricsAgentBackend.class);
        metricsAgent.addMetricAgent(agent1);

        long startTime = metricsAgent.startTimer();
        metricsAgent.stopTimer("test.metric", startTime);

        verify(agent1).logTimedEvent(anyString(), anyLong());
    }

    @Test
    public void testTimerTwoAgents() {
        MultipleMetricsAgentImpl metricsAgent = new MultipleMetricsAgentImpl();

        MetricsAgentBackend agent1 = mock(MetricsAgentBackend.class);
        metricsAgent.addMetricAgent(agent1);

        MetricsAgentBackend agent2 = mock(MetricsAgentBackend.class);
        metricsAgent.addMetricAgent(agent2);

        long startTime = metricsAgent.startTimer();
        metricsAgent.stopTimer("test.metric", startTime);

        verify(agent1).logTimedEvent(anyString(), anyLong());
        verify(agent2).logTimedEvent(anyString(), anyLong());
    }
}
