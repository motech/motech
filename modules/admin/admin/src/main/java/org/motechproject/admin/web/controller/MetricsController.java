package org.motechproject.admin.web.controller;



import org.motechproject.event.metrics.StatsdAgentConfigurationData;
import org.motechproject.event.metrics.StatsdAgentBackend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/*Class used to pass statsd metrics agent config values from agent to UI, it also controls
saving this values to agent
 */
@Controller
public class MetricsController {
    @Autowired
    private StatsdAgentBackend statsdAgentBackend;

    @RequestMapping(value = "/metrics", method = RequestMethod.GET)
    @ResponseBody
    public StatsdAgentConfigurationData getMetricsData() {
        StatsdAgentConfigurationData statsdAgentConfigurationData = new StatsdAgentConfigurationData();
        statsdAgentConfigurationData.setGenerateHostBasedStats(statsdAgentBackend.isGenerateHostBasedStats());
        statsdAgentConfigurationData.setServerHost(statsdAgentBackend.getServerHost());
        statsdAgentConfigurationData.setServerPort(statsdAgentBackend.getServerPort());
        return statsdAgentConfigurationData;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/metrics/save", method = RequestMethod.POST)
    public void saveStatsdAgentConfig(@RequestBody StatsdAgentConfigurationData
                                              statsdAgentConfigurationData) {
        int serverPort = statsdAgentConfigurationData.getServerPort();
        String serverHost = statsdAgentConfigurationData.getServerHost();
        boolean generateHostBasedStats = statsdAgentConfigurationData.isGenerateHostBasedStats();

        //Setting new values before save to file
        statsdAgentBackend.setServerPort(serverPort);
        statsdAgentBackend.setServerHost(serverHost);
        statsdAgentBackend.setGenerateHostBasedStats(generateHostBasedStats);
        statsdAgentBackend.saveProperties();

    }
}