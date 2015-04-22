package org.motechproject.server.osgi.status;

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
    private Map<String, String> errorsByBundle = new HashMap<>();

    public List<String> getStartedBundles() {
        return startedBundles;
    }

    public void setStartedBundles(List<String> startedBundles) {
        this.startedBundles = startedBundles;
    }

    public Map<String, String> getErrorsByBundle() {
        return errorsByBundle;
    }

    public void setErrorsByBundle(Map<String, String> errorsByBundle) {
        this.errorsByBundle = errorsByBundle;
    }

    public void addStartedBundle(String bundleSymbolicName) {
        startedBundles.add(bundleSymbolicName);
    }

    public void removeStartedBundle(String bundleSymbolicName) {
        startedBundles.remove(bundleSymbolicName);
    }

    public void addError(String bundleSymbolicName, String error) {
        errorsByBundle.put(bundleSymbolicName, error);
    }
}
