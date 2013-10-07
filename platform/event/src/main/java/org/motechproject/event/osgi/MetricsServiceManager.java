package org.motechproject.event.osgi;

import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.motechproject.event.metrics.MetricsAgent;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * The <code>MetricsServiceManager</code> class is used for calling Metrics Agent's functions.
 * It is done using metric's OSGi service which is obtained during runtime.
 */
@Component("metricsServiceManager")
public class MetricsServiceManager implements OsgiServiceLifecycleListener {

    private boolean serviceAvailable;
    private MetricsAgent metricsAgent;

    @Override
    public void bind(Object service, Map serviceProperties) {
        metricsAgent = (MetricsAgent) service;
        serviceAvailable = true;
    }

    @Override
    public void unbind(Object service, Map serviceProperties) {
        serviceAvailable = false;
        metricsAgent = null;
    }

    public boolean isServiceAvailable() {
        return serviceAvailable;
    }

    public MetricsAgent getService() {
        return metricsAgent;
    }
}
