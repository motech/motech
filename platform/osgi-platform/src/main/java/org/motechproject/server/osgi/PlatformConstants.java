package org.motechproject.server.osgi;

/**
 * Created by pawel on 4/16/14.
 */
public final class PlatformConstants {
    public static final String HTTP_BRIDGE_BUNDLE = "org.apache.felix.http.bridge";
    public static final String MDS_ENTITIES_BUNDLE = "org.motechproject.motech-platform-dataservices-entities";
    public static final String MDS_BUNDLE_PREFIX = "org.motechproject.motech-platform-dataservices";

    public static final String DB_SERVICE_CLASS = "org.motechproject.commons.couchdb.service.CouchDbManager";
    public static final String SECURITY_BUNDLE_SYMBOLIC_NAME = "org.motechproject.motech-platform-web-security";

    public static final String STARTUP_TOPIC = "org/motechproject/osgi/event/STARTUP";
    public static final String MDS_STARTUP_TOPIC = "org/motechproject/osgi/event/MDS_START";

    public static final String PLATFORM_BUNDLE_SYMBOLIC_NAME = "org.motechproject.motech-osgi-platform";
    public static final String PLATFORM_BUNDLE_PREFIX = "org.motechproject.motech-platform-";
    public static final String MOTECH_BUNDLE_PREFIX = "org.motechproject.motech-";



    private PlatformConstants() {
    }
}
