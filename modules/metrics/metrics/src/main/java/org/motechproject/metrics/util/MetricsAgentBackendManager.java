package org.motechproject.metrics.util;

import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.motechproject.metrics.MetricsAgentBackend;
import org.motechproject.metrics.impl.MultipleMetricsAgentImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for managing metrics agent backend implementations available as services
 * Allows getting available and used implementations as well as setting new ones as used
 */

@Component("metricsAgentBackendImplManager")
public class MetricsAgentBackendManager implements OsgiServiceLifecycleListener {

    private Map<String, MetricsAgentBackend> availableMetricsAgentImplementations;

    private final List<String> implementationsEnabledByDefault = new ArrayList<>(Arrays.asList(new String[] {"Logging"}));

    @Autowired
    private MultipleMetricsAgentImpl metricsAgent;

    public MetricsAgentBackendManager() {
        availableMetricsAgentImplementations = new HashMap<>();
    }

    @Override
    public void bind(Object service, Map serviceProperties) {
        MetricsAgentBackend impl = (MetricsAgentBackend) service;

        availableMetricsAgentImplementations.put(impl.getImplementationName(), impl);

        if (implementationsEnabledByDefault.contains(impl.getImplementationName())) {
            metricsAgent.addMetricAgent(impl);
        }
    }

    @Override
    public void unbind(Object service, Map serviceProperties) {
        availableMetricsAgentImplementations.remove(((MetricsAgentBackend) service).getImplementationName());
    }

    public Map<String, MetricsAgentBackend> getAvailableMetricsAgentImplementations() {
        return availableMetricsAgentImplementations;
    }

    public List<String> getUsedMetricsAgents() {
        List<String> names = new ArrayList<>();

        for (MetricsAgentBackend elem : metricsAgent.getMetricsAgents()) {
            names.add(elem.getImplementationName());
        }

        return names;
    }

    public void setMetricsAgents(List<String> selected) {
        List<MetricsAgentBackend> metricsAgents = new ArrayList<>();

        for (String elem : selected) {
            metricsAgents.add(availableMetricsAgentImplementations.get(elem));
        }

        metricsAgent.setMetricsAgents(metricsAgents);
    }
}
