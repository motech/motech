package org.motechproject.metrics.ut.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.metrics.MetricRegistryInitializer;
import org.motechproject.metrics.config.ConsoleReporterConfig;
import org.motechproject.metrics.config.GraphiteReporterConfig;
import org.motechproject.metrics.config.MetricsConfig;
import org.motechproject.metrics.config.MetricsConfigFacade;
import org.motechproject.metrics.util.ConfigUtil;
import org.motechproject.metrics.web.MetricsConfigController;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class MetricsConfigControllerTest {
    @Mock
    private MetricsConfigFacade metricsConfigFacade;

    @Mock
    private MetricRegistryInitializer metricRegistryInitializer;

    @Captor
    ArgumentCaptor<MetricsConfig> configCaptor;

    private MockMvc controller;

    @Before
    public void setUp() {
        controller = MockMvcBuilders.standaloneSetup(new MetricsConfigController(metricsConfigFacade, metricRegistryInitializer)).build();
    }

    @Test
    public void shouldReturnTimeUnits() throws Exception {
        Map<String, TimeUnit[]> expected = new HashMap<>();
        expected.put("data", TimeUnit.values());

        controller.perform(get("/config/timeUnits"))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expected)));
    }

    @Test
    public void shouldDeserializeMetricsConfig() throws Exception {
        MetricsConfig configToSave = ConfigUtil.getDefaultConfig();

        controller.perform(post("/config")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ObjectMapper().writeValueAsBytes(configToSave)))
                .andExpect(status().isOk());

        verify(metricsConfigFacade).saveMetricsConfig(configCaptor.capture());

        verify(metricRegistryInitializer).init();

        MetricsConfig config = configCaptor.getValue();
        ConsoleReporterConfig consoleConfig = config.getConsoleReporterConfig();
        GraphiteReporterConfig graphiteConfig = config.getGraphiteReporterConfig();

        assertEquals(true, config.isMetricsEnabled());

        assertEquals(true, consoleConfig.isEnabled());
        assertEquals(1, consoleConfig.getFrequency());
        assertEquals(TimeUnit.SECONDS, consoleConfig.getFrequencyUnit());
        assertEquals(TimeUnit.MILLISECONDS, consoleConfig.getConvertRates());
        assertEquals(TimeUnit.SECONDS, consoleConfig.getConvertDurations());

        assertEquals(true, graphiteConfig.isEnabled());
        assertEquals("http://foo.com/graphite", graphiteConfig.getServerUri());
        assertEquals(2003, graphiteConfig.getServerPort());
        assertEquals(1, graphiteConfig.getFrequency());
        assertEquals(TimeUnit.SECONDS, graphiteConfig.getFrequencyUnit());
        assertEquals(TimeUnit.MILLISECONDS, graphiteConfig.getConvertRates());
        assertEquals(TimeUnit.SECONDS, graphiteConfig.getConvertDurations());
    }
}
