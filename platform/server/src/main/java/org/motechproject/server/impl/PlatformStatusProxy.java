package org.motechproject.server.impl;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.motechproject.server.ex.StatusProxyException;
import org.motechproject.server.osgi.status.PlatformStatus;
import org.motechproject.server.osgi.status.PlatformStatusManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Created by pawel on 22.04.15.
 */
public class PlatformStatusProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformStatusProxy.class);

    private final BundleContext bundleContext;

    private Object statusManager;

    public PlatformStatusProxy(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public PlatformStatus getCurrentStatus() {
        Object statusManager = getStatusManager();

        if (statusManager == null) {
            LOGGER.debug("Status manager unavailable");
            return new PlatformStatus();
        } else {
            try {
                Object status = MethodUtils.invokeExactMethod(statusManager, "getCurrentStatus", new Object[0]);
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
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        PlatformStatus newStatus = new PlatformStatus();

        newStatus.setStartedBundles((List<String>) PropertyUtils.getProperty(originalStatus, "startedBundles"));
        newStatus.setErrorsByBundle((Map<String, String>) PropertyUtils.getProperty(originalStatus, "errorsByBundle"));

        return newStatus;
    }
}
