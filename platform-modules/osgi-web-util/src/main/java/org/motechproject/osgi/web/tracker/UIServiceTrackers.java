package org.motechproject.osgi.web.tracker;

import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.tracker.internal.UIServiceTracker;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.eclipse.gemini.blueprint.util.OsgiStringUtils.nullSafeSymbolicName;

/**
 * The registry that handles {@link UIServiceTracker} instances created
 * for bundles with <code>Blueprint-Enabled</code> header in their manifest.
 *
 * @see UIServiceTracker
 */
public class UIServiceTrackers {

    private static final Logger LOGGER = LoggerFactory.getLogger(UIServiceTrackers.class);

    private static final String MODULE_REGISTRATION_DATA = "moduleRegistrationData";

    private final Map<String, UIServiceTracker> trackers = new HashMap<>();

    /**
     * Constructs the registry and registers a bundle listener that will remove trackers for bundles that are stopping.
     * @param bundleContext the bundle context used to register the bundle listener
     */
    public UIServiceTrackers(BundleContext bundleContext) {
        bundleContext.addBundleListener(new SynchronousBundleListener() {
            @Override
            public void bundleChanged(BundleEvent event) {
                if (event.getType() == BundleEvent.STOPPING) {
                    removeTrackerFor(event.getBundle());
                }
            }
        });
    }

    /**
     * Creates a {@link UIServiceTracker} for the given bundle. The {@link org.motechproject.osgi.web.ModuleRegistrationData}
     * from the provided Spring context of the bundle will be used for registering the UI.
     * @param bundle the bundle to create the tracker for
     * @param applicationContext the Spring context of the bundle
     * @return the newly created {@link UIServiceTracker} instance
     */
    public UIServiceTracker addTrackerFor(Bundle bundle, ApplicationContext applicationContext) {

        if (!applicationContext.containsBean(MODULE_REGISTRATION_DATA)) {
            LOGGER.warn("Bean moduleRegistrationData not found in {}", nullSafeSymbolicName(bundle));
            return null;
        }

        LOGGER.debug("Bean moduleRegistrationData found for bundle {}", nullSafeSymbolicName(bundle));

        ModuleRegistrationData moduleRegistrationData = (ModuleRegistrationData) applicationContext.getBean(MODULE_REGISTRATION_DATA);

        final UIServiceTracker uiServiceTracker = new UIServiceTracker(bundle.getBundleContext(), moduleRegistrationData);
        trackers.put(nullSafeSymbolicName(bundle), uiServiceTracker);
        uiServiceTracker.start();

        LOGGER.debug("UI Service Tracker registered for {}", nullSafeSymbolicName(bundle));

        return uiServiceTracker;
}

    /**
     * Closes and removes the {@link UIServiceTracker} for the bundle.
     * @param bundle the bundle to remove the tracker for
     * @return the closed and removed tracker
     */
    public UIServiceTracker removeTrackerFor(Bundle bundle) {
        String symbolicName = bundle.getSymbolicName();

        LOGGER.debug("Removing tracker for: {}", symbolicName);

        UIServiceTracker removedTracker = trackers.remove(symbolicName);

        if (removedTracker != null) {
            removedTracker.close();
            LOGGER.debug("Tracker for {} was closed", symbolicName);
        } else {
            LOGGER.debug("No tracker registered for {}", symbolicName);
        }

        return removedTracker;
    }

    /**
     * Checks whether this registry already has a tracker for the given bundle.
     * @param bundle the bundle to check
     * @return true if the registry has a tracker for the bundle, false otherwise
     */
    public boolean isBeingTracked(Bundle bundle) {
        return trackers.containsKey(nullSafeSymbolicName(bundle));
    }
}
