package org.motechproject.context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.metrics.MetricsAgent;
import org.motechproject.server.event.EventListenerRegistry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testPlatformServerApplicationContext.xml"})
public class ContextIT {

    @Test
    public void testMetricsAgentContext() throws Exception {
        MetricsAgent metricsAgent = Context.getInstance().getMetricsAgent();
        assertNotNull(metricsAgent);
    }

    @Test
    public void testEventListenerRegistry() {
        EventListenerRegistry eventListenerRegistry = Context.getInstance().getEventListenerRegistry();
        assertNotNull(eventListenerRegistry);
    }
}
