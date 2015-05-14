package org.motechproject.server.impl;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.motechproject.server.ex.StatusProxyException;
import org.motechproject.server.osgi.status.PlatformStatus;
import org.motechproject.server.osgi.status.PlatformStatusManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * A proxy for dealing with the @{link PlatformStatusManager} OSGi service from the osgi-platform bundle.
 * Since the server uses a different classloader, we must dance around the manager using reflections.
 */
public class PlatformStatusProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformStatusProxy.class);

    private final BundleContext bundleContext;

    private Object statusManager;

    public PlatformStatusProxy(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    /**
     * Retrieves the current platform status. The object returned will be created using the current (webapp) classlaoder,
     * preventing casting issues.
     * @return the current status of the platform, for safe use with the webapp classloader
     */
    public PlatformStatus getCurrentStatus() {
        Object mgr = getStatusManager();

        if (mgr == null) {
            LOGGER.debug("Status manager unavailable");
            return new PlatformStatus();
        } else {
            try {
                Object status = MethodUtils.invokeExactMethod(mgr, "getCurrentStatus", new Object[0]);
                return convertStatusBetweenClassLoaders(status);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new StatusProxyException("Unable to retrieve status from the manager instance", e);
            }
        }
    }

    private Object getStatusManager() {
        if (statusManager == null) {
            ServiceReference ref = bundleContext.getServiceReference(PlatformStatusManager.class.getName());
            statusManager = (ref == null) ? null : bundleContext.getService(ref);
        }
        return statusManager;
    }

    private PlatformStatus convertStatusBetweenClassLoaders(Object originalStatus)
            throws InvocationTargetException, IllegalAccessException {
        PlatformStatus newStatus = new PlatformStatus();

        BeanUtils.copyProperties(newStatus, originalStatus);

        return newStatus;
    }
}
