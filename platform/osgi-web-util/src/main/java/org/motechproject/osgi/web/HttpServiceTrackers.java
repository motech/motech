package org.motechproject.osgi.web;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.eclipse.gemini.blueprint.util.OsgiStringUtils.nullSafeSymbolicName;

public class HttpServiceTrackers {

    final private Map<String, HttpServiceTracker> trackers = new HashMap<>();


    public HttpServiceTracker addTrackerFor(Bundle bundle) {
        HttpServiceTracker httpServiceTracker
                = new HttpServiceTracker(bundle.getBundleContext(), getResourceMapping(new BundleHeaders(bundle)));
        trackers.put(nullSafeSymbolicName(bundle), httpServiceTracker);
        httpServiceTracker.start();
        bundle.getBundleContext().addBundleListener(new SynchronousBundleListener() {
            @Override
            public void bundleChanged(BundleEvent event) {
                if (event.getType() == BundleEvent.STOPPING) {
                    String symbolicName = nullSafeSymbolicName(event.getBundle());
                    HttpServiceTracker trackedRemoved = trackers.remove(symbolicName);
                    if (trackedRemoved != null) {
                        trackedRemoved.unregister();
                        trackedRemoved.close();
                    }
                }
            }
        });
        return httpServiceTracker;
    }

    public boolean isBeingTracked(Bundle bundle) {
        return trackers.containsKey(nullSafeSymbolicName(bundle));
    }

    public HttpServiceTracker removeTrackerFor(Bundle bundle) {
        return trackers.remove(nullSafeSymbolicName(bundle));
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
