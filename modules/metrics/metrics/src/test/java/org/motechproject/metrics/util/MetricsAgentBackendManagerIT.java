package org.motechproject.metrics.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.metrics.impl.LoggingAgentBackendImpl;
import org.motechproject.metrics.impl.StatsdAgentBackendImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.apache.commons.collections.MapUtils.isEmpty;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class MetricsAgentBackendManagerIT {

    @Autowired
    private MetricsAgentBackendManager metricsManager;

    @Test
    public void shouldProperlyBindAndUnbindImplementations() {
        LoggingAgentBackendImpl loggingAgent = new LoggingAgentBackendImpl();
        StatsdAgentBackendImpl statsdAgent = new StatsdAgentBackendImpl();

        metricsManager.bind(loggingAgent, null);
        metricsManager.bind(statsdAgent, null);

        assertEquals(2, metricsManager.getAvailableMetricsAgentImplementations().size());

        metricsManager.unbind(statsdAgent, null);
        metricsManager.unbind(loggingAgent, null);

        assertTrue(isEmpty(metricsManager.getAvailableMetricsAgentImplementations()));
    }
}
