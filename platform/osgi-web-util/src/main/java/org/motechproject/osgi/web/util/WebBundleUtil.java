package org.motechproject.osgi.web.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.ArrayList;
import java.util.List;

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
            if (StringUtils.equals(symbolicName, bundle.getSymbolicName())) {
                return bundle;
            }
        }

        return null;
    }

    /**
     * Returns the list of bundles symbolic names.
     *
     * @param context  the context of the bundle, not null
     * @return  the list of the bundles symbolic names
     */
    public static List<String> getSymbolicNames(BundleContext context) {
        Bundle[] bundles = context.getBundles();
        List<String> list = new ArrayList<>();

        if (ArrayUtils.isNotEmpty(bundles)) {
            for (Bundle bundle : bundles) {
                CollectionUtils.addIgnoreNull(list, bundle.getSymbolicName());
            }
        }

        return list;
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
        return bundle.getHeaders().get(headerContextPath);
    }
}
