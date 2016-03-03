package org.motechproject.osgi.web.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for easing bundle related operations/searches.
 */
public final class WebBundleUtil {

    private WebBundleUtil() {
    }

    /**
     * Does a search for a bundle with a matching Bundle-Name header in its manifest.
     * Note that if there two bundles installed with the same name, the first one found will be returned.
     * @param bundleContext the bundle context used for the search
     * @param name the Bundle-Name header value of the bundle we are searching for
     * @return the matching bundle, or null if there is no such bundle
     */
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

    /**
     * Does a search for a bundle with a matching symbolic name.
     * Note that if there two bundles installed with the same symbolic name, the first one found will be returned.
     * @param bundleContext the bundle context used for the search
     * @param symbolicName symbolic name of the bundle we are looking for
     * @return the matching bundle, or null if no such bundle exists
     */
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

    /**
     * Returns the context file location for the bundle, by using reading its <b>Context-File</b> header.
     * @param bundle the bundle for which we want to retrieve the context file location for
     * @return the location of the context file, or null if it is not defined
     */
    public static String getContextLocation(Bundle bundle) {
        final String contextLocation = getHeaderValue("Context-File", bundle);
        return contextLocation != null ? contextLocation : "META-INF/osgi/*.xml";
    }

    /**
     * Returns the HTTP context path for the bundle, by using reading its <b>Context-Path</b> header.
     * @param bundle the bundle for which we want to retrieve the context path for
     * @return the context path file, or null if it is not defined
     */
    public static String getContextPath(Bundle bundle) {
        return "/" + getModuleId(bundle);
    }

    /**
     * Returns an id for a given bundle for internal use. This is the context-path header value, or
     * the symbolic name if the former is not defined.
     * @param bundle the bundle for which we need an id
     * @return the id for the bundle
     */
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
