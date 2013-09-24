package org.motechproject.metrics.web;


import org.motechproject.metrics.StatsdAgentBackend;
import org.motechproject.metrics.StatsdAgentConfigurationData;
import org.motechproject.metrics.util.MetricsAgentBackendManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
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

    @RequestMapping(value = "/settings/getAll", method = RequestMethod.GET)
    @ResponseBody
    public StatsdAgentConfigurationData getMetricsData() {
        StatsdAgentConfigurationData statsdAgentConfigurationData = new StatsdAgentConfigurationData();
        statsdAgentConfigurationData.setGenerateHostBasedStats(statsdAgentBackend.isGenerateHostBasedStats());
        statsdAgentConfigurationData.setServerHost(statsdAgentBackend.getServerHost());
        statsdAgentConfigurationData.setServerPort(statsdAgentBackend.getServerPort());
        statsdAgentConfigurationData.setGraphiteUrl(statsdAgentBackend.getGraphiteUrl());
        return statsdAgentConfigurationData;
    }

    @RequestMapping(value = "/settings/getGraphiteUrl", method = RequestMethod.GET)
    @ResponseBody
    public String getGraphiteUrl() {
        return statsdAgentBackend.getGraphiteUrl();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/save", method = RequestMethod.POST)
    public void saveStatsdAgentConfig(@RequestBody StatsdAgentConfigurationData
                                              statsdAgentConfigurationData) {
        int serverPort = statsdAgentConfigurationData.getServerPort();
        String serverHost = statsdAgentConfigurationData.getServerHost();
        boolean generateHostBasedStats = statsdAgentConfigurationData.isGenerateHostBasedStats();
        String graphiteUrl = statsdAgentConfigurationData.getGraphiteUrl();

        //Setting new values before save to file
        statsdAgentBackend.setServerPort(serverPort);
        statsdAgentBackend.setServerHost(serverHost);
        statsdAgentBackend.setGenerateHostBasedStats(generateHostBasedStats);
        statsdAgentBackend.setGraphiteUrl(graphiteUrl);
        statsdAgentBackend.saveProperties();
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
}
