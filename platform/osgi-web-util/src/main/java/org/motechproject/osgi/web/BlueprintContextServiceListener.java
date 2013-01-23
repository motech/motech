package org.motechproject.osgi.web;

import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isNotBlank;


public class BlueprintContextServiceListener implements ServiceListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlueprintContextServiceListener.class);
    public static final String CONTEXT_SERVICE_NAME = "org.springframework.context.service.name";

    private Map<String, HttpServiceTracker> httpServiceTrackers = new HashMap<>();


    @Override
    public void serviceChanged(ServiceEvent event) {
        String symbolicName = OsgiStringUtils.nullSafeSymbolicName(event.getServiceReference().getBundle());
        if (event.getType() == ServiceEvent.REGISTERED) {
            LOGGER.error("REGISTERED == " + symbolicName);
            serviceRegistered(event);
        }
        if (event.getType() == ServiceEvent.UNREGISTERING) {
            LOGGER.error("UNREGISTERED == " + symbolicName);
            serviceUnregistered(event);
        }
        if (event.getType() == ServiceEvent.MODIFIED) {
            LOGGER.error("MODIFIED == " + symbolicName);
        }
    }

    private void serviceUnregistered(ServiceEvent event) {
        String serviceName = getServiceName(event.getServiceReference());
        HttpServiceTracker serviceTracker = httpServiceTrackers.get(serviceName);
        if (serviceTracker != null) {
            serviceTracker.unregister();
            serviceTracker.close();
            httpServiceTrackers.remove(serviceTracker);
        }
    }

    private void serviceRegistered(ServiceEvent event) {
        ServiceReference serviceReference = event.getServiceReference();
        Bundle bundle = serviceReference.getBundle();
        if (!new BundleHeaders(bundle).isBluePrintEnabled()) {
            return;
        }
        String serviceName = getServiceName(serviceReference);
        for (String name : httpServiceTrackers.keySet()) {
            LOGGER.error("Tracker added for " + name);
        }
        if (isApplicationContextService(serviceReference) && !trackerStartedFor(serviceName)) {
            Map<String, String> resourceMapping = getResourceMapping(bundle);
            HttpServiceTracker serviceTracker = new HttpServiceTracker(bundle.getBundleContext(), resourceMapping);
            httpServiceTrackers.put(serviceName, serviceTracker);
            serviceTracker.start();
            LOGGER.info(String.format("Application context started for %s .. yoooooo.....", serviceName));
        }
    }

    private boolean trackerStartedFor(String serviceName) {
        return httpServiceTrackers.containsKey(serviceName);
    }

    private boolean isApplicationContextService(ServiceReference serviceReference) {
        List<String> publishedInterfacesForService = Arrays.asList((String[]) serviceReference.getProperty(Constants.OBJECTCLASS));
        if (publishedInterfacesForService.isEmpty()) {
            return false;
        }
        Bundle bundle = serviceReference.getBundle();
        String symbolicName = OsgiStringUtils.nullSafeSymbolicName(bundle);
        return publishedInterfacesForService.contains(ApplicationContext.class.getName())
                && symbolicName.equals(getServiceName(serviceReference));
    }

    private Map<String, String> getResourceMapping(Bundle bundle) {
        BundleHeaders headers = new BundleHeaders(bundle);
        Map<String, String> resourceMapping = new HashMap<>();
        if (isNotBlank(headers.getResourcePath())) {
            resourceMapping.put(headers.getResourcePath(), "/webapp");
        }
        return resourceMapping;
    }

    private String getServiceName(ServiceReference serviceReference) {
        return (String) serviceReference.getProperty(CONTEXT_SERVICE_NAME);
    }

}


