package org.eclipse.core.runtime;

import org.motechproject.server.osgi.OsgiFrameworkService;
import org.motechproject.server.osgi.OsgiListener;

import java.io.IOException;
import java.net.URL;

public class FileLocator {

    public static URL resolve(URL url) throws IOException {
        if ("bundle".equals(url.getProtocol())) {
            OsgiFrameworkService osgiService = OsgiListener.getOsgiService();
            String bundleLocation = osgiService.getBundleLocationByBundleId(url.getHost());
            if (bundleLocation != null) {
                return new URL("jar:" + bundleLocation + "!" + url.getFile());
            }
        }
        return url;
    }
}
