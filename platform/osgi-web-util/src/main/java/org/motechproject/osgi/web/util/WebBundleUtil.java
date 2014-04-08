package org.motechproject.osgi.web.util;

import org.apache.commons.lang.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Utility class that's purpose is easing bundle related operations/searches.
 */
public final class WebBundleUtil {

    private WebBundleUtil() {
    }

    public static Bundle findBundleByName(BundleContext bundleContext, String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Name cannot be blank");
        }

        for (Bundle bundle : bundleContext.getBundles()) {
            BundleHeaders headers = new BundleHeaders(bundle);
            if (StringUtils.equals(name, headers.getName())) {
                return bundle;

            }
        }

        return null;
    }

    public static Bundle findBundleBySymbolicName(BundleContext bundleContext, String symbolicName) {
        if (StringUtils.isBlank(symbolicName)) {
            throw new IllegalArgumentException("Name cannot be blank");
        }

        for (Bundle bundle : bundleContext.getBundles()) {
            BundleHeaders headers = new BundleHeaders(bundle);
            if (StringUtils.equals(symbolicName, headers.getSymbolicName())) {
                return bundle;

            }
        }

        return null;
    }

    public static String getContextLocation(Bundle bundle) {
        final String contextLocation = getHeaderValue("Context-File", bundle);
        return contextLocation != null ? contextLocation : "META-INF/osgi/*.xml";
    }

    public static String getContextPath(Bundle bundle) {
        return "/" + getModuleId(bundle);
    }

    public static String getModuleId(Bundle bundle) {
        final String headerValue = getHeaderValue("Context-Path", bundle);
        return ((headerValue != null) ? headerValue : bundle.getSymbolicName());
    }

    private static String getHeaderValue(String headerContextPath, Bundle bundle) {
        if (bundle.getHeaders() == null) {
            return null;
        }
        return (String) bundle.getHeaders().get(headerContextPath);
    }
}
