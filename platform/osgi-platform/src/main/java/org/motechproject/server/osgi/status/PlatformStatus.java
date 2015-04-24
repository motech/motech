package org.motechproject.server.osgi.status;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.server.osgi.PlatformConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Carries information about the status of the current startup.
 */
public class PlatformStatus implements Serializable {

    private static final long serialVersionUID = -8488315045279371605L;

    private List<String> startedBundles = new ArrayList<>();
    private Map<String, String> contextErrorsByBundle = new HashMap<>();
    private Map<String, String> bundleErrorsByBundle = new HashMap<>();
    private int startupProgressPercentage;

    public static final int REQUIRED_FOR_STARTUP = 10;

    public List<String> getStartedBundles() {
        return startedBundles;
    }

    public void setStartedBundles(List<String> startedBundles) {
        this.startedBundles = startedBundles;
        updateStartupProgressPercentage();
    }

    public Map<String, String> getContextErrorsByBundle() {
        return contextErrorsByBundle;
    }

    public void setContextErrorsByBundle(Map<String, String> contextErrorsByBundle) {
        this.contextErrorsByBundle = contextErrorsByBundle;
    }

    public Map<String, String> getBundleErrorsByBundle() {
        return bundleErrorsByBundle;
    }

    public void setBundleErrorsByBundle(Map<String, String> bundleErrorsByBundle) {
        this.bundleErrorsByBundle = bundleErrorsByBundle;
    }

    public void addStartedBundle(String bundleSymbolicName) {
        startedBundles.add(bundleSymbolicName);
        updateStartupProgressPercentage();
    }

    public void removeStartedBundle(String bundleSymbolicName) {
        startedBundles.remove(bundleSymbolicName);
    }

    public void addContextError(String bundleSymbolicName, String error) {
        contextErrorsByBundle.put(bundleSymbolicName, error);
    }

    public void addBundleError(String bundleSymbolicName, String error) {
        bundleErrorsByBundle.put(bundleSymbolicName, error);
    }

    public int getStartupProgressPercentage() {
        return startupProgressPercentage;
    }

    @JsonProperty
    public boolean inFatalError() {
        return containsPlatformBundleError(bundleErrorsByBundle) || containsPlatformBundleError(contextErrorsByBundle);
    }

    @JsonProperty
    public boolean errorsOccurred() {
        return !bundleErrorsByBundle.isEmpty() || !contextErrorsByBundle.isEmpty();
    }

    private void updateStartupProgressPercentage() {
        startupProgressPercentage = (int) Math.floor((double) startedBundles.size() / REQUIRED_FOR_STARTUP * 100);

        // additional modules don't count, so that we won't get over 100%
        if (startupProgressPercentage > 100) {
            startupProgressPercentage = 100;
        }
    }

    private boolean containsPlatformBundleError(Map<String, String> errors) {
        for (String symbolicName : errors.keySet()) {
            if (symbolicName.startsWith(PlatformConstants.PLATFORM_BUNDLE_PREFIX)) {
                return true;
            }
        }
        return false;
    }
}
