package org.motechproject.metrics.web;


import org.motechproject.metrics.service.StatsdAgentBackend;
import org.motechproject.metrics.domain.ConfigProperty;
import org.motechproject.metrics.domain.PropertyType;
import org.motechproject.metrics.exception.ValidationException;
import org.motechproject.metrics.util.MetricsAgentBackendManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*Class used to pass statsd metrics agent config values from agent to UI, it also controls
saving this values to agent
 */
@Controller
public class MetricsController {
    @Autowired
    private StatsdAgentBackend statsdAgentBackend;

    @Autowired
    private MetricsAgentBackendManager metricsAgentBackendManager;

    @RequestMapping(value = "/settings/getGraphiteUrl", method = RequestMethod.GET)
    @ResponseBody
    public String getGraphiteUrl() {
        return statsdAgentBackend.getGraphiteUrl();
    }

    @RequestMapping(value = "/backend/available", method = RequestMethod.GET)
    @ResponseBody
    public Set<String> getAllAvailableImplementations() {
        return metricsAgentBackendManager.getAvailableMetricsAgentImplementations().keySet();
    }

    @RequestMapping(value = "/backend/used", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getUsedImplementations() {
        return metricsAgentBackendManager.getUsedMetricsAgents();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/backend/used", method = RequestMethod.POST)
    public void setUsedImplementations(@RequestBody List<String> selected) {
        metricsAgentBackendManager.setMetricsAgents(selected);
    }

    @RequestMapping(value = "/backend/{implName}/settings", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, ConfigProperty> getBackendSettings(@PathVariable final String implName) {
        return metricsAgentBackendManager.getSettings(implName);
    }

    @RequestMapping(value = "/backend/{implName}/settings", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void setBackendSettings(@PathVariable final String implName, @RequestBody Map<String, Map<String, String>> config) {
        Map<String, ConfigProperty> configProperties = convertToConfigProperties(config);

        metricsAgentBackendManager.saveSettings(implName, configProperties);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(ValidationException e) {
        return e.getMessage();
    }

    private Map<String, ConfigProperty> convertToConfigProperties(Map<String, Map<String, String>> config) {
        Map<String, ConfigProperty> ret = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : config.entrySet()) {
            Map<String, String> configProperty = entry.getValue();
            String displayName = configProperty.get("displayName");
            PropertyType type = PropertyType.valueOf(configProperty.get("type"));
            String value = configProperty.get("value");

            ret.put(entry.getKey(), new ConfigProperty(displayName, type, value));
        }

        return ret;
    }
}
