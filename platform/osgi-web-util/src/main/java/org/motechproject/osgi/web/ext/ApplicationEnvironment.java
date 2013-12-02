package org.motechproject.osgi.web.ext;

public final class ApplicationEnvironment {
    public static final String ENVIRONMENT = "ENVIRONMENT";
    public static final String DEVELOPMENT = "DEVELOPMENT";

    private ApplicationEnvironment() {
    }

    public static String getEnvironment() {
        return System.getenv(ENVIRONMENT);
    }

    public static boolean isInDevelopmentMode() {
        return DEVELOPMENT.equals(getEnvironment());
    }

    public static String getModulePath(BundleName bundleName) {
        return System.getenv(bundleName.underscore());
    }
}
