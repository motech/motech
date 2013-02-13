package org.motechproject.osgi.web;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.eclipse.gemini.blueprint.util.OsgiStringUtils.nullSafeSymbolicName;

public class UIServiceTrackers {

    private static final Logger LOGGER = LoggerFactory.getLogger(UIServiceTracker.class);
    private static final String MODULE_REGISTRATION_DATA = "moduleRegistrationData";

    final private Map<String, UIServiceTracker> trackers = new HashMap<>();

    public UIServiceTracker addTrackerFor(Bundle bundle, ApplicationContext applicationContext) {

        if (!applicationContext.containsBean(MODULE_REGISTRATION_DATA)) {
            LOGGER.warn("bean moduleRegistrationData not found");
            return null;
        }

        LOGGER.info("bean moduleRegistrationData found for bundle " + nullSafeSymbolicName(bundle));

        ModuleRegistrationData moduleRegistrationData = (ModuleRegistrationData) applicationContext.getBean(MODULE_REGISTRATION_DATA);

        final UIServiceTracker uiServiceTracker = new UIServiceTracker(bundle.getBundleContext(), moduleRegistrationData);
        trackers.put(nullSafeSymbolicName(bundle), uiServiceTracker);
        uiServiceTracker.start();
        bundle.getBundleContext().addBundleListener(new SynchronousBundleListener() {
            @Override
            public void bundleChanged(BundleEvent event) {
                if (event.getType() == BundleEvent.STOPPING) {
                    String symbolicName = event.getBundle().getSymbolicName();
                    UIServiceTracker removedTracker = trackers.remove(symbolicName);
                    if (removedTracker != null) {
                        removedTracker.close();
                    }
                }
            }
        });
        return uiServiceTracker;
    }

    public UIServiceTracker removeTrackerFor(Bundle bundle) {
        return trackers.remove(nullSafeSymbolicName(bundle));
    }


}
