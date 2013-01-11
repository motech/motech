package org.motechproject.osgi.web.util;

import org.osgi.framework.Bundle;

public final class WebBundleUtil {

    private WebBundleUtil(){}

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
