package org.motechproject.server.osgi.util;

/**
 * Collection of constants related to the Platform startup and bundle management.
 */
public final class PlatformConstants {

    public static final String HTTP_BRIDGE_BUNDLE = "org.apache.felix.http.bridge";
    public static final String FELIX_FRAMEWORK_BUNDLE = "org.apache.felix.framework";
    public static final String MDS_ENTITIES_BUNDLE = "org.motechproject.motech-platform-dataservices-entities";
    public static final String MDS_BUNDLE_NAME = "org.motechproject.motech-platform-dataservices";

    public static final String SECURITY_SYMBOLIC_NAME = "org.motechproject.motech-platform-web-security";
    public static final String SERVER_SYMBOLIC_NAME = "org.motechproject.motech-platform-server-bundle";

    public static final String STARTUP_TOPIC = "org/motechproject/osgi/event/STARTUP";
    public static final String MDS_STARTUP_TOPIC = "org/motechproject/osgi/event/MDS_START";

    public static final String PAX_IT_SYMBOLIC_NAME = "org.motechproject.motech-pax-it";
    public static final String PLATFORM_BUNDLE_SYMBOLIC_NAME = "org.motechproject.motech-osgi-platform";
    public static final String PLATFORM_BUNDLE_PREFIX = "org.motechproject.motech-platform-";
    public static final String MOTECH_PACKAGE = "org.motechproject";

    private PlatformConstants() {
    }
}
