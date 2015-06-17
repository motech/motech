package org.motechproject.server.osgi.util;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a logical bundle type. Used for determining startup order.
 */
public enum BundleType {

    /**
     * This a 3rd party bundle, a library.
     */
    THIRD_PARTY_BUNDLE,
    /**
     * The HTTP bridge bundle, required for HTTP access to MOTECH.
     */
    HTTP_BUNDLE,
    /**
     * Bundles that MDS depends on - commons bundles, osgi-web-util, server-api and config-core.
     */
    PLATFORM_BUNDLE_PRE_MDS,
    /**
     * The Motech DataServices bundle. Required special treatment due to its nature of changing class definitions on
     * the fly.
     */
    MDS_BUNDLE,
    /**
     * Bundles required for Web-security to start. These are event and server-config.
     */
    PLATFORM_BUNDLE_PRE_WS,
    /**
     * The web-security bundle. Gets special treatment due to its crucial nature.
     */
    WS_BUNDLE,
    /**
     * All platform bundles not included in the other platform bundle types.
     */
    PLATFORM_BUNDLE_POST_WS,
    /**
     * A regular Motech module, starts after the platform.
     */
    MOTECH_MODULE,
    /**
     * A fragment bundle, this should not be started(per OSGi spec), they attach themselves to the host.
     */
    FRAGMENT_BUNDLE,
    /**
     * The OSGi framework bundle
      */
    FRAMEWORK_BUNDLE;

    public static final Set<String> PLATFORM_PRE_MDS_BUNDLES = new HashSet<>(Arrays.asList(
            "commons-api", "commons-sql", "commons-date", "osgi-web-util", "server-api", "config-core", "event"
    ));

    public static final Set<String> PLATFORM_PRE_WS_BUNDLES = new HashSet<>(Arrays.asList(
        "server-config"
    ));

    public static BundleType forBundle(Bundle bundle) {
        String symbolicName = bundle.getSymbolicName();

        if (isFragmentBundle(bundle)) {
            return BundleType.FRAGMENT_BUNDLE;
        } else if (symbolicName == null || PlatformConstants.PAX_IT_SYMBOLIC_NAME.equals(symbolicName)) {
            return BundleType.THIRD_PARTY_BUNDLE;
        } else if (symbolicName.startsWith(PlatformConstants.MDS_BUNDLE_PREFIX)) {
            return BundleType.MDS_BUNDLE;
        } else if (symbolicName.equals(PlatformConstants.SECURITY_SYMBOLIC_NAME)) {
            return BundleType.WS_BUNDLE;
        } else if (PlatformConstants.HTTP_BRIDGE_BUNDLE.equals(symbolicName)) {
            return BundleType.HTTP_BUNDLE;
        } else if (symbolicName.startsWith(PlatformConstants.PLATFORM_BUNDLE_PREFIX)) {
            return getPlatformBundleType(symbolicName);
        } else if (importsExportsMotechPackage(bundle)) {
            return BundleType.MOTECH_MODULE;
        } else if (PlatformConstants.FELIX_FRAMEWORK_BUNDLE.equals(symbolicName)) {
            return FRAMEWORK_BUNDLE;
        } else {
            return BundleType.THIRD_PARTY_BUNDLE;
        }
    }

    private static BundleType getPlatformBundleType(String symbolicName) {
        String moduleName = symbolicName.substring(PlatformConstants.PLATFORM_BUNDLE_PREFIX.length());

        if (PLATFORM_PRE_MDS_BUNDLES.contains(moduleName)) {
            return PLATFORM_BUNDLE_PRE_MDS;
        } else if (PLATFORM_PRE_WS_BUNDLES.contains(moduleName)) {
            return PLATFORM_BUNDLE_PRE_WS;
        } else {
            return PLATFORM_BUNDLE_POST_WS;
        }
    }

    private static boolean importsExportsMotechPackage(Bundle bundle) {
        String imports = bundle.getHeaders().get("Import-Package");
        String exports = bundle.getHeaders().get("Export-Package");

        return (imports != null && imports.contains(PlatformConstants.MOTECH_PACKAGE)) ||
                (exports != null && exports.contains(PlatformConstants.MOTECH_PACKAGE));
    }

    public static boolean isFragmentBundle(Bundle bundle) {
        return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
    }
}
