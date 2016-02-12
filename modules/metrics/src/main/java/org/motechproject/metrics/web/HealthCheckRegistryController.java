package org.motechproject.metrics.web;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.SortedMap;

import static org.motechproject.metrics.security.Roles.HAS_VIEW_METRICS_ROLE;

/**
 * Initiates health checks from the user interface and returns the result.
 */
@Controller
@PreAuthorize(HAS_VIEW_METRICS_ROLE)
public class HealthCheckRegistryController {
    private HealthCheckRegistry healthCheckRegistry;

    public HealthCheckRegistryController() {}

    @Autowired
    public HealthCheckRegistryController(HealthCheckRegistry healthCheckRegistry) {
        this.healthCheckRegistry = healthCheckRegistry;
    }

    /**
     * Runs all registered health checks and returns the results.
     *
     * @return the health check results
     */
    @RequestMapping("/healthChecks")
    @ResponseBody
    public SortedMap<String, HealthCheck.Result> runHealthChecks() {
        return healthCheckRegistry.runHealthChecks();
    }
}
