package org.motechproject.server.osgi.status;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.server.osgi.PlatformConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the status of the platform startup. It contains information about
 * which bundles were started by Gemini Blueprint, it also carries information
 * about both OSGi level and Spring context level errors that occurred in the system.
 */
public class PlatformStatus implements Serializable {

    private static final long serialVersionUID = -8488315045279371605L;

    private List<String> startedBundles = new ArrayList<>();
    private Map<String, String> contextErrorsByBundle = new HashMap<>();
    private Map<String, String> bundleErrorsByBundle = new HashMap<>();
    private int startupProgressPercentage;

    public static final int REQUIRED_FOR_STARTUP = 10;

    /**
     * Returns started bundles. To be considered started a bundle must had its Spring context successfully created by Gemini Blueprint.
     * We do not track non-blueprint enabled bundles here.
     * @return the started bundles.
     */
    public List<String> getStartedBundles() {
        return startedBundles;
    }

    /**
     * Sets the started bundles. To be considered started a bundle must had its Spring context successfully created by Gemini Blueprint.
     * We do not track non-blueprint enabled bundles here.
     * @param startedBundles the started bundles.
     */
    public void setStartedBundles(List<String> startedBundles) {
        this.startedBundles = startedBundles;
        updateStartupProgressPercentage();
    }

    /**
     * Returns context errors that occurred in the system in a form of a map. The keys in the map are bundle symbolic names.
     * The values are error messages. Context errors are errors that occurred during the creation of the Blueprint context.
     * @return context errors that occurred in the system
     */
    public Map<String, String> getContextErrorsByBundle() {
        return contextErrorsByBundle;
    }

    /**
     * Sets the context errors that occurred in the system in a form of a map. The keys in the map are bundle symbolic names.
     * The values are error messages. Context errors are errors that occurred during the creation of the Blueprint context.
     * Failed bundles will be removed from the started bundle list.
     * @param contextErrorsByBundle  context errors that occurred in the system
     */
    public void setContextErrorsByBundle(Map<String, String> contextErrorsByBundle) {
        this.contextErrorsByBundle = contextErrorsByBundle;
        startedBundles.removeAll(contextErrorsByBundle.keySet());
    }

    /**
     * Returns bundles errors that occurred in the system in a form of a map. The keys in the map are bundle symbolic names.
     * The values are error messages. Bundle errors are errors that occurred on the OSGi level, and prevented the bundle itself from starting.
     * @return bundle errors that occurred in the system
     */
    public Map<String, String> getBundleErrorsByBundle() {
        return bundleErrorsByBundle;
    }

    /**
     * Sets the bundles errors that occurred in the system in a form of a map. The keys in the map are bundle symbolic names.
     * The values are error messages. Bundle errors are errors that occurred on the OSGi level, and prevented the bundle itself from starting.
     * Failed bundles will be removed from the started bundle list.
     * @param bundleErrorsByBundle bundle errors that occurred in the system
     */
    public void setBundleErrorsByBundle(Map<String, String> bundleErrorsByBundle) {
        this.bundleErrorsByBundle = bundleErrorsByBundle;
        startedBundles.removeAll(bundleErrorsByBundle.keySet());
    }

    /**
     * Returns the startup progress in percent. The startup progress represents the number of started bundles in relation to
     * the number of bundles that is required for the server to be fully started. This is capped at 100%.
     * @return the startup progress in percent
     */
    public int getStartupProgressPercentage() {
        return startupProgressPercentage;
    }

    /**
     * Returns true if we faced a fatal error during startup, meaning a platform bundle failed to start. This means a startup failure.
     * @return true if we occurred such an error, false otherwise
     */
    @JsonProperty
    public boolean inFatalError() {
        return containsPlatformBundleError(bundleErrorsByBundle) || containsPlatformBundleError(contextErrorsByBundle);
    }

    /**
     * Returns true if we faced any errors context/bundle. This doesn't necessarily mean a startup failure.
     * @return true if errors occurred, false otherwise
     */
    @JsonProperty
    public boolean errorsOccurred() {
        return !bundleErrorsByBundle.isEmpty() || !contextErrorsByBundle.isEmpty();
    }

    void addStartedBundle(String bundleSymbolicName) {
        startedBundles.add(bundleSymbolicName);
        updateStartupProgressPercentage();
    }

    void removeStartedBundle(String bundleSymbolicName) {
        startedBundles.remove(bundleSymbolicName);
    }

    void addContextError(String bundleSymbolicName, String error) {
        contextErrorsByBundle.put(bundleSymbolicName, error);
        startedBundles.remove(bundleSymbolicName);
    }

    void addBundleError(String bundleSymbolicName, String error) {
        bundleErrorsByBundle.put(bundleSymbolicName, error);
        startedBundles.remove(bundleSymbolicName);
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
