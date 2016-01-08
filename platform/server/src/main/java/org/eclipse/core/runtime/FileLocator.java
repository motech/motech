package org.eclipse.core.runtime;

import org.motechproject.server.impl.OsgiFrameworkService;
import org.motechproject.server.impl.OsgiListener;

import java.io.IOException;
import java.net.URL;

/**
 * This class allows to load urls starting with bundle://.
 * The openmrs-api module requires this hack for loading its xml classpath contexts in OSGi.
 */
public final class FileLocator {

    private FileLocator() {
    }

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
