package org.motechproject.event.metrics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.metrics.impl.LoggingAgentBackendImpl;
import org.motechproject.event.metrics.impl.MultipleMetricsAgentImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class MetricsIT {
    @Autowired
    private MetricsAgent metricsAgent;

    @Before
    public void setup() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testMetricsAgent() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Key", "Value");

        long startTime = metricsAgent.startTimer();
        metricsAgent.logEvent("test.event", params);
        metricsAgent.logEvent("test.event2");
        metricsAgent.stopTimer("timed.event", startTime);
    }

    @Test
    public void shouldEnableLogginAgentOnlyByDefault() throws Exception {
        MultipleMetricsAgentImpl impl = (MultipleMetricsAgentImpl) metricsAgent;

        assertEquals(1, impl.getMetricsAgents().size());
        assertEquals(LoggingAgentBackendImpl.class, impl.getMetricsAgents().get(0).getClass());
    }
}
