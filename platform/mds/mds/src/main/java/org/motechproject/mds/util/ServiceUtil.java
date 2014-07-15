package org.motechproject.mds.util;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * The <code>ServiceUtil</code> class provides utility methods to work with services
 */
public final class ServiceUtil {
    private ServiceUtil() {}

    public static <S> S getServiceForInterfaceName(BundleContext bundleContext, String interfaceName) {
        ServiceReference ref = bundleContext.getServiceReference(interfaceName);

        return ref == null ? null : (S) bundleContext.getService(ref);
    }
}
