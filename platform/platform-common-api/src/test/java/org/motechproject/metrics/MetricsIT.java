package org.motechproject.metrics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.metrics.impl.LoggingAgentBackendImpl;
import org.motechproject.metrics.impl.MultipleMetricsAgentImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: 3/28/11
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationPlatformCommonAPI.xml"})
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
