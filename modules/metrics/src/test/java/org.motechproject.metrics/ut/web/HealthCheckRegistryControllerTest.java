package org.motechproject.metrics.ut.web;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.metrics.web.HealthCheckRegistryController;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.util.SortedMap;
import java.util.TreeMap;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class HealthCheckRegistryControllerTest {
    @Mock
    private HealthCheckRegistry healthCheckRegistry;

    private HealthCheckRegistryController healthCheckRegistryController;

    private MockMvc controller;

    @Before
    public void setUp() {
        healthCheckRegistryController = new HealthCheckRegistryController(healthCheckRegistry);
        controller = MockMvcBuilders.standaloneSetup(healthCheckRegistryController).build();
    }

    @Test
    public void shouldReturnHealthChecks() throws Exception {
        SortedMap<String, HealthCheck.Result> expected = generateHealthChecks();

        SortedMap<String, HealthCheck.Result> healthChecks = new TreeMap<>();
        healthChecks.put("healthy", HealthCheck.Result.healthy());
        healthChecks.put("unhealthy", HealthCheck.Result.unhealthy("error message"));
        when(healthCheckRegistry.runHealthChecks()).thenReturn(healthChecks);

        controller.perform(get("/healthChecks"))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expected)));
    }

    private SortedMap<String, HealthCheck.Result> generateHealthChecks() {
        SortedMap<String, HealthCheck.Result> results = new TreeMap<>();

        results.put("healthy", HealthCheck.Result.healthy());
        results.put("unhealthy", HealthCheck.Result.unhealthy("error message"));

        return results;
    }
}
