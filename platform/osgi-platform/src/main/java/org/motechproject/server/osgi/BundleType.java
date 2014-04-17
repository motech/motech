package org.motechproject.server.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public enum BundleType {
    THIRD_PARTY_BUNDLE,
    HTTP_BUNDLE,
    PLATFORM_BUNDLE_PRE_MDS,
    MDS_BUNDLE,
    PLATFORM_BUNDLE_PRE_WS,
    WS_BUNDLE,
    PLATFORM_BUNDLE_POST_WS,
    MOTECH_MODULE,
    FRAGMENT_BUNDLE,
    PAX_EXAM_BUNDLE;

    public static final Set<String> PLATFORM_PRE_MDS_BUNDLES = new HashSet<>(Arrays.asList(
            "commons-api", "commons-sql", "commons-couchdb", "commons-date", "osgi-web-util", "server-api", "config-core"
    ));

    public static final Set<String> PLATFORM_PRE_WS_BUNDLES = new HashSet<>(Arrays.asList(
        "event", "server-config"
    ));

    public static BundleType forBundle(Bundle bundle) {
        String symbolicName = bundle.getSymbolicName();

        if (isFragmentBundle(bundle)) {
            return BundleType.FRAGMENT_BUNDLE;
        } else if (symbolicName == null || PlatformConstants.PAX_IT_SYMBOLIC_NAME.equals(symbolicName)) {
            return BundleType.THIRD_PARTY_BUNDLE;
        } else if (symbolicName.startsWith(PlatformConstants.MDS_BUNDLE_PREFIX)) {
            return BundleType.MDS_BUNDLE;
        } else if (symbolicName.equals(PlatformConstants.SECURITY_BUNDLE_SYMBOLIC_NAME)) {
            return BundleType.WS_BUNDLE;
        } else if (PlatformConstants.HTTP_BRIDGE_BUNDLE.equals(symbolicName)) {
            return BundleType.HTTP_BUNDLE;
        } else if (symbolicName.startsWith(PlatformConstants.PLATFORM_BUNDLE_PREFIX)) {
            return getPlatformBundleType(symbolicName);
        } else if (symbolicName.startsWith(PlatformConstants.MOTECH_BUNDLE_PREFIX)) {
            return BundleType.MOTECH_MODULE;
        } else if (symbolicName.startsWith(PlatformConstants.PAX_EXAM_PREFIX)) {
            return BundleType.PAX_EXAM_BUNDLE;
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

    public static boolean isFragmentBundle(Bundle bundle) {
        return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
    }
}
