package org.motechproject.osgi.web.extension;

import org.motechproject.osgi.web.bundle.BundleName;

/**
 * Utility class for handling the {@code ENVIRONMENT} system variable. The only meaningful value for the
 * variable at the moment {@code DEVELOPMENT}, which will cause MOTECH to load static resources from
 * disk paths instead of jar classpaths. It also allows resolving of these disk paths for given bundle name.
 */
public final class ApplicationEnvironment {

    /**
     * The name of the variable controlling whether we are in development mode.
     */
    public static final String ENVIRONMENT = "ENVIRONMENT";

    /**
     * The value representing development mode.
     */
    public static final String DEVELOPMENT = "DEVELOPMENT";

    private ApplicationEnvironment() {
    }

    /**
     * @return the value of the environment variable
     */
    public static String getEnvironment() {
        return System.getenv(ENVIRONMENT);
    }

    /**
     * Checks whether we are in development mode, meaning that the {@code ENVIRONMENT} system variable was set to {@code DEVELOPMENT}.
     * @return true if we are in development mode, false otherwise
     */
    public static boolean isInDevelopmentMode() {
        return DEVELOPMENT.equals(getEnvironment());
    }

    /**
     * Returns the root disk path from which resources for the bundle with a given name should be loaded.
     * This is controlled by system variables with names equal to bundle symbolic names with dots and dashes replaced
     * with underscores. For example, the path for a bundle with the symbolic name {@code org.motechproject.cms-lite} is
     * controlled by the system variable {@code org_motechproject_cms_lite}.
     * @param bundleName the name of bundle
     * @return the root path from which to load bundle resources or null if it is not set
     */
    public static String getModulePath(BundleName bundleName) {
        return System.getenv(bundleName.underscore());
    }
}
