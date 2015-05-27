package org.motechproject.osgi.web.util;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import java.util.Dictionary;

/**
 * A convenience class for reading bundle headers.
 */
public class BundleHeaders {

    public static final String BLUEPRINT_ENABLED = "Blueprint-Enabled";
    public static final String CONTEXT_PATH = "Context-Path";
    public static final String RESOURCE_PATH = "Resource-Path";

    private final Dictionary headers;

    /**
     * Constructs this class using a bundle from which the given bundle context from.
     * @param bundleContext the context of the bundle for which we want to read headers
     */
    public BundleHeaders(BundleContext bundleContext) {
        this(bundleContext.getBundle());
    }

    /**
     * Constructs this class using a given bundle.
     * @param bundle the bundle for which we want to read headers
     */
    public BundleHeaders(Bundle bundle) {
        headers = bundle.getHeaders();
    }

    /**
     * Retrieves the header value for a given key.
     * @param key the key for the header
     * @return the value of the header
     */
    public Object get(Object key) {
        return headers.get(key);
    }

    /**
     * Returns the header representing the http path under which the servlet for this bundle should be published.
     * @return the <b>Context-Path</b> header
     */
    public String getContextPath() {
        return getStringValue(CONTEXT_PATH);
    }

    /**
     * Returns the header representing the http path under which the static resources (from the /webapp directory) for this bundle should be published.
     * @return the <b>Resource-Path</b> header
     */
    public String getResourcePath() {
        return getStringValue(RESOURCE_PATH);
    }

    /**
     * Reads the symbolic name of the bundle.
     * @return the <b>Bundle-SymbolicName</b> header
     */
    public String getSymbolicName() {
        return getStringValue(Constants.BUNDLE_SYMBOLICNAME);
    }

    /**
     * Reads the name of the bundle.
     * @return the <b>Bundle-Name</b> header
     */
    public String getName() {
        return getStringValue(Constants.BUNDLE_NAME);
    }

    /**
     * Reads the version of the bundle.
     * @return the <b>Bundle-Version</b> header
     */
    public String getVersion() {
        return getStringValue(Constants.BUNDLE_VERSION);
    }

    /**
     * Gets the header value after casting it to a String.
     * @param key the key for which the header should be retrieved
     * @return the header value as a String
     */
    public String getStringValue(String key) {
        return (String) get(key);
    }

    /**
     * Checks if the bundle is Blueprint enabled, meaning its "Blueprint-Enabled" header is set to <code>true</code>
     * @return true if the bundle is Blueprint enabled, false otherwise
     */
    public boolean isBluePrintEnabled() {
        return Boolean.valueOf(getStringValue(BLUEPRINT_ENABLED));
    }
}
