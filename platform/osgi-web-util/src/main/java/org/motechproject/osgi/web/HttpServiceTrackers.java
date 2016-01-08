package org.motechproject.osgi.web;

import org.motechproject.osgi.web.util.BundleHeaders;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.eclipse.gemini.blueprint.util.OsgiStringUtils.nullSafeSymbolicName;

/**
 * This is responsible for creating and keeping track of {@link org.motechproject.osgi.web.HttpServiceTracker} instances.
 */
public class HttpServiceTrackers {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServiceTrackers.class);

    private final Map<String, HttpServiceTracker> trackers = new HashMap<>();

    /**
     * Creates this http tracker registry and registers a bundle listener,
     * that will close the trackers for a bundle once it is stopped.
     *
     * @param bundleContext the context used for registering the bundle listener
     */
    public HttpServiceTrackers(BundleContext bundleContext) {
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
     * Creates an {@link org.motechproject.osgi.web.HttpServiceTracker} instance for the given bundle.
     * @param bundle the bundle for which the tracker should be created
     * @return the newly created tracker
     */
    public HttpServiceTracker addTrackerFor(Bundle bundle) {
        LOGGER.debug("Adding HTTP service tracker for: {}", nullSafeSymbolicName(bundle));

        HttpServiceTracker httpServiceTracker
                = new HttpServiceTracker(bundle.getBundleContext(), getResourceMapping(new BundleHeaders(bundle)));
        trackers.put(nullSafeSymbolicName(bundle), httpServiceTracker);
        httpServiceTracker.start();

        LOGGER.debug("Registered HTTP service tracker for: {}", nullSafeSymbolicName(bundle));

        return httpServiceTracker;
    }

    /**
     * Checks whether an {@link org.motechproject.osgi.web.HttpServiceTracker} exists for the given bundle.
     * @param bundle the bundle to check
     * @return true if a tracker exists, false otherwise
     */
    public boolean isBeingTracked(Bundle bundle) {
        return trackers.containsKey(nullSafeSymbolicName(bundle));
    }

    /**
     * Removes a tracker for a given bundle. The tracker is also unregistered and closed cleanly.
     * @param bundle the bundle to remove the tracker for
     * @return the closed tracker instance, dropped from this registry
     */
    public HttpServiceTracker removeTrackerFor(Bundle bundle) {
        final String symbolicName = nullSafeSymbolicName(bundle);

        LOGGER.debug("Removing tracker for: {}", symbolicName);

        HttpServiceTracker trackerToRemove = trackers.remove(symbolicName);

        if (trackerToRemove != null) {
            trackerToRemove.unregister();
            trackerToRemove.close();
            LOGGER.debug("Tracker for {} closed", symbolicName);
        } else {
            LOGGER.debug("No tracker registered for: {}", symbolicName);
        }

        return trackerToRemove;
    }

    private Map<String, String> getResourceMapping(BundleHeaders headers) {
        final String resourcePath = getResourcePath(headers);
        Map<String, String> resourceMapping = new HashMap<>();
        if (isNotBlank(resourcePath)) {
            resourceMapping.put(resourcePath, "/webapp");
        }
        return resourceMapping;
    }

    private String getResourcePath(BundleHeaders headers) {
        String path = headers.getResourcePath();
        if (isBlank(path) || path.startsWith("/")) {
            return path;
        }
        return format("/%s", path);
    }
}
