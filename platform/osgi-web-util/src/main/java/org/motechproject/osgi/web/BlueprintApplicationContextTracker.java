package org.motechproject.osgi.web;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import static org.eclipse.gemini.blueprint.util.OsgiStringUtils.nullSafeSymbolicName;

public class BlueprintApplicationContextTracker extends ServiceTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlueprintApplicationContextTracker.class);

    private static final String APPLICATION_CONTEXT_SERVICE_NAME = "org.springframework.context.service.name";

    private HttpServiceTrackers httpServiceTrackers;
    private final UIServiceTrackers uiServiceTrackers;

    public BlueprintApplicationContextTracker(BundleContext context) {
        super(context, ApplicationContext.class.getName(), null);
        this.httpServiceTrackers = new HttpServiceTrackers();
        this.uiServiceTrackers = new UIServiceTrackers();
        registerServiceTrackersAsService(context);
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        Object applicationContext = super.addingService(serviceReference);
        Bundle bundle = serviceReference.getBundle();

        if (!isBlueprintEnabledBundle(bundle)) {
            return applicationContext;
        }
        String symbolicName = nullSafeSymbolicName(bundle);
        String contextServiceName = getServiceName(serviceReference);
        if (!symbolicName.equals(contextServiceName) || httpServiceTrackers.isBeingTracked(bundle)) {
            return applicationContext;
        }

        httpServiceTrackers.addTrackerFor(bundle);
        uiServiceTrackers.addTrackerFor(bundle, (ApplicationContext) applicationContext);
        return applicationContext;
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        super.removedService(reference, service);
        Bundle bundle = reference.getBundle();
        if (!isBlueprintEnabledBundle(bundle)) {
            return;
        }
        String symbolicName = nullSafeSymbolicName(bundle);
        String contextServiceName = getServiceName(reference);

        if (symbolicName.equals(contextServiceName)) {
            LOGGER.info("Removed service " + bundle.getSymbolicName());
            httpServiceTrackers.removeTrackerFor(bundle);
            uiServiceTrackers.removeTrackerFor(bundle);
        }
    }

    private void registerServiceTrackersAsService(BundleContext context) {
        context.registerService(HttpServiceTrackers.class.getName(), httpServiceTrackers, null);
        context.registerService(UIServiceTrackers.class.getName(), uiServiceTrackers, null);
    }


    private boolean isBlueprintEnabledBundle(Bundle bundle) {
        return new BundleHeaders(bundle).isBluePrintEnabled();
    }

    private String getServiceName(ServiceReference serviceReference) {
        return (String) serviceReference.getProperty(APPLICATION_CONTEXT_SERVICE_NAME);
    }


}
