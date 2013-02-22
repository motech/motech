package org.motechproject.osgi.web.ext;

public class ApplicationEnvironment {

    public static final String ENVIRONMENT = "ENVIRONMENT";
    public static final String DEVELOPMENT = "DEVELOPMENT";

    public String getEnvironment() {
        return System.getenv(ENVIRONMENT);
    }

    public boolean isInDevelopmentMode() {
        return DEVELOPMENT.equals(getEnvironment());
    }

    public String getModulePath(BundleName bundleName) {
        return System.getenv(bundleName.underscore());
    }
}
