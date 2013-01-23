package org.motechproject.osgi.web;

import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class BlueprintApplicationContextTracker extends ServiceTracker {

    private static final String APPLICATION_CONTEXT_SERVICE_NAME = "org.springframework.context.service.name";

    private final List<String> servicesTracked = new ArrayList<>();

    public BlueprintApplicationContextTracker(BundleContext context) {
        super(context, ApplicationContext.class.getName(), null);
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        Object service = super.addingService(serviceReference);
        Bundle bundle = serviceReference.getBundle();
        String bundleSymbolicName = OsgiStringUtils.nullSafeSymbolicName(bundle);
        BundleHeaders headers = new BundleHeaders(bundle);
        if (!headers.isBluePrintEnabled()) {
            return service;
        }
        final String serviceName = getServiceName(serviceReference);
        if (!bundleSymbolicName.equals(serviceName) || isTrackerRegisteredFor(serviceName)) {
            return service;
        }
        registerServiceTrackerForBundle(bundle, serviceName);
        return service;
    }

    private void registerServiceTrackerForBundle(Bundle bundle, String serviceName) {
        final HttpServiceTracker httpServiceTracker
                = new HttpServiceTracker(bundle.getBundleContext(), getResourceMapping(new BundleHeaders(bundle)));
        servicesTracked.add(serviceName);
        httpServiceTracker.start();
        bundle.getBundleContext().addBundleListener(new SynchronousBundleListener() {
            @Override
            public void bundleChanged(BundleEvent event) {
                if (event.getType() == BundleEvent.STOPPING) {
                    String symbolicName = event.getBundle().getSymbolicName();
                    httpServiceTracker.unregister();
                    httpServiceTracker.close();
                    servicesTracked.remove(symbolicName);
                }
            }
        });
    }

    private Map<String, String> getResourceMapping(BundleHeaders headers) {
        final String resourcePath = getResourcePath(headers);
        Map<String, String> resourceMapping = new HashMap<>();
        if (isNotBlank(resourcePath)) {
            resourceMapping.put(resourcePath, "/webapp");
        }
        return resourceMapping;
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        super.removedService(reference, service);
        String serviceName = getServiceName(reference);
        servicesTracked.remove(serviceName);
    }

    private String getServiceName(ServiceReference serviceReference) {
        return (String) serviceReference.getProperty(APPLICATION_CONTEXT_SERVICE_NAME);
    }

    private String getResourcePath(BundleHeaders headers) {
        String path = headers.getResourcePath();
        if (isBlank(path) || path.startsWith("/")) {
            return path;
        }
        return format("/%s", path);
    }

    private boolean isTrackerRegisteredFor(String serviceName) {
        return servicesTracked.contains(serviceName);
    }

}
