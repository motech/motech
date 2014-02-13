package org.motechproject.osgi.web;

import org.motechproject.osgi.web.util.BundleHeaders;
import org.motechproject.server.api.BundleLoadingException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.List;

import static org.eclipse.gemini.blueprint.util.OsgiStringUtils.nullSafeSymbolicName;

/**
 * The <code>BlueprintApplicationContextTracker</code> class tracks application contexts, which are registered as services.
 */

public class BlueprintApplicationContextTracker extends ApplicationContextTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlueprintApplicationContextTracker.class);

    private static final String APPLICATION_CONTEXT_SERVICE_NAME = "org.springframework.context.service.name";
    private static final String OSGI_WEB_UTIL = "org.motechproject.motech-platform-osgi-web-util";
    private Log4JBundleLoader logBundleLoader;
    private HttpServiceTrackers httpServiceTrackers;
    private final UIServiceTrackers uiServiceTrackers;

    public BlueprintApplicationContextTracker(BundleContext context) {
        super(context);
        this.httpServiceTrackers = new HttpServiceTrackers();
        this.uiServiceTrackers = new UIServiceTrackers();
        registerServiceTrackersAsService(context);
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        ApplicationContext applicationContext = (ApplicationContext) super.addingService(serviceReference);
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
        uiServiceTrackers.addTrackerFor(bundle, applicationContext);

        synchronized (this) {
            if (OSGI_WEB_UTIL.equals(symbolicName)) {
                logBundleLoader = applicationContext.getBean(Log4JBundleLoader.class);
            }
            try {
                BundleRegister bundleRegister = BundleRegister.getInstance();
                bundleRegister.addBundle(bundle);

                if (logBundleLoader != null) {
                    List<Bundle> bundleList = bundleRegister.getBundleList();
                    for (Bundle bundleElement : bundleList) {
                        logBundleLoader.loadBundle(bundleElement);
                    }
                    bundleRegister.getBundleList().clear();
                }
            } catch (BundleLoadingException e) {
                LOGGER.error("Failed adding log4j configuration for [" + serviceReference.getBundle().getLocation() + "]\n" + e.getMessage());
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
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
        return bundle != null && new BundleHeaders(bundle).isBluePrintEnabled();
    }

    private String getServiceName(ServiceReference serviceReference) {
        return (String) serviceReference.getProperty(APPLICATION_CONTEXT_SERVICE_NAME);
    }


}
