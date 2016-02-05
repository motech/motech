package org.motechproject.metrics.web;

import org.motechproject.metrics.MetricRegistryInitializer;
import org.motechproject.metrics.config.MetricsConfig;
import org.motechproject.metrics.config.MetricsConfigFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.motechproject.metrics.security.Roles.HAS_MANAGE_METRICS_ROLE;

/**
 * Sends and receives configuration to and from the user interface.
 */
@Controller
@PreAuthorize(HAS_MANAGE_METRICS_ROLE)
public class MetricsConfigController {
    private MetricsConfigFacade metricsConfigFacade;
    private MetricRegistryInitializer metricRegistryInitializer;

    public MetricsConfigController() {}

    @Autowired
    public MetricsConfigController(MetricsConfigFacade metricsConfigFacade,
                                   MetricRegistryInitializer metricRegistryInitializer) {
        this.metricsConfigFacade = metricsConfigFacade;
        this.metricRegistryInitializer = metricRegistryInitializer;
    }

    /**
     * Retrieves the current configuration.
     *
     * @return the current configuration
     */
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    @ResponseBody
    public MetricsConfig getMetricsConfig() {
        return metricsConfigFacade.getMetricsConfig();
    }

    /**
     * Updates configuration sent from the user interface and reinitializes the module with those new settings.
     *
     * @param metricsConfig the updated configuration
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/config", method = RequestMethod.POST)
    public void saveSettings(@RequestBody MetricsConfig metricsConfig) {
        metricsConfigFacade.saveMetricsConfig(metricsConfig);
        metricRegistryInitializer.init();
    }

    /**
     * Sends the module's accepted time units to the user interface.
     *
     * @return an object with the accepted time units
     */
    @RequestMapping(value = "/config/timeUnits", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, TimeUnit[]> getAcceptableTimeUnits() {
        Map<String, TimeUnit[]> acceptableTimeUnits = new HashMap<>();
        acceptableTimeUnits.put("data", TimeUnit.values());
        return acceptableTimeUnits;
    }
}
