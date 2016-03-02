package org.motechproject.osgi.web.tracker.impl;

import org.apache.commons.lang.StringUtils;
import org.motechproject.osgi.web.bundle.BundleRegister;
import org.motechproject.osgi.web.bundle.Log4JBundleLoader;
import org.motechproject.osgi.web.exception.BundleConfigurationLoadingException;
import org.motechproject.osgi.web.service.UIFrameworkService;
import org.motechproject.osgi.web.tracker.ApplicationContextTracker;
import org.motechproject.osgi.web.tracker.HttpServiceTrackers;
import org.motechproject.osgi.web.tracker.UIServiceTrackers;
import org.motechproject.osgi.web.tracker.internal.HttpServiceTracker;
import org.motechproject.osgi.web.tracker.internal.UIServiceTracker;
import org.motechproject.osgi.web.util.BundleHeaders;
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
 * The <code>BlueprintApplicationContextTracker</code> class tracks application contexts, which are registered as services
 * by the Gemini extender. This is the main processor for MOTECH modules. For each module it will create an
 * {@link HttpServiceTracker} and a {@link UIServiceTracker}.
 * These trackers will be responsible for registering the module with {@link org.osgi.service.http.HttpService} (so
 * that they can expose an HTTP endpoint) and the {@link UIFrameworkService} (so that they can register their UI)
 * respectively. This module also uses the {@link Log4JBundleLoader} for loading log4j
 * configuration files from the registered modules. The processing is only performed for bundles that have the
 * <code>Blueprint-Enabled</code> header in their manifest.
 */

public class BlueprintApplicationContextTracker extends ApplicationContextTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlueprintApplicationContextTracker.class);

    private static final String IGNORE_DB_VAR = "org.motechproject.logging.ignoreBundles";

    private static final String APPLICATION_CONTEXT_SERVICE_NAME = "org.springframework.context.service.name";
    private static final String OSGI_WEB_UTIL = "org.motechproject.motech-platform-osgi-web-util";
    private Log4JBundleLoader logBundleLoader;
    private HttpServiceTrackers httpServiceTrackers;
    private final UIServiceTrackers uiServiceTrackers;

    public BlueprintApplicationContextTracker(BundleContext context) {
        super(context);
        this.httpServiceTrackers = new HttpServiceTrackers(context);
        this.uiServiceTrackers = new UIServiceTrackers(context);
        registerServiceTrackersAsService(context);
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        ApplicationContext applicationContext = (ApplicationContext) super.addingService(serviceReference);
        Bundle bundle = serviceReference.getBundle();
        String symbolicName = nullSafeSymbolicName(bundle);

        LOGGER.info("Processing context for: {}", symbolicName);

        if (!isBlueprintEnabledBundle(bundle)) {
            LOGGER.debug("Bundle {} is not Blueprint Enabled", symbolicName);
            return applicationContext;
        }

        String contextServiceName = getServiceName(serviceReference);

        if (!symbolicName.equals(contextServiceName)) {
            LOGGER.warn("Bundle symbolic name [{}] does not match the service name of the context [{}]", symbolicName, contextServiceName);
            return applicationContext;
        }
        if (httpServiceTrackers.isBeingTracked(bundle)) {
            LOGGER.debug("Bundle {} is already tracked", symbolicName);
            return applicationContext;
        }

        LOGGER.debug("Registering trackers for {}", symbolicName);

        httpServiceTrackers.addTrackerFor(bundle);
        uiServiceTrackers.addTrackerFor(bundle, applicationContext);

        LOGGER.debug("Trackers registered for {}", symbolicName);

        // scan for logger configuration, we don't want to do this in tests
        if (!StringUtils.equalsIgnoreCase("true", System.getProperty(IGNORE_DB_VAR))) {

            LOGGER.debug("Scanning bundle {} for logger configuration", symbolicName);

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
                } catch (BundleConfigurationLoadingException e) {
                    LOGGER.error("Failed adding log4j configuration for [" + serviceReference.getBundle().getLocation() + "]\n" + e.getMessage());
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                }
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
