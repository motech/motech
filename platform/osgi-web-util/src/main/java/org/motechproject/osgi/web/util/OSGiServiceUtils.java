package org.motechproject.osgi.web.util;

import org.motechproject.osgi.web.exception.ServiceWaitInterruptedException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Utility class for retrieving OSGi services.
 */
public final class OSGiServiceUtils {

    private static final long WAIT_STEP = 1000;

    /**
     * Retrieves the service of the given class from the bundle context.
     * This will retrieve the service with highest priority if there are multiple instances of the service.
     * @param bundleContext the bundleContext which will be used for service retrieval
     * @param clazz the class of the service to be retrieved
     * @param <T> the type of the service to be returned
     * @return the service found or {@code null} if there is no such service in the bundle context
     */
    public static <T> T findService(BundleContext bundleContext, Class<T> clazz) {
        return findService(bundleContext, clazz.getName());
    }

    /**
     * Retrieves the service of the given class from the bundle context.
     * This will retrieve the service with highest priority if there are multiple instances of the service.
     * @param bundleContext the bundleContext which will be used for service retrieval
     * @param className the class name of the service to be retrieved
     * @param <T> the type of the service to be returned
     * @return the service found or {@code null} if there is no such service in the bundle context
     */
    public static <T> T findService(BundleContext bundleContext, String className) {
        ServiceReference<T> ref = (ServiceReference<T>) bundleContext.getServiceReference(className);
        return ref == null ? null : bundleContext.getService(ref);
    }

    /**
     * Retrieves the service of the given class from the bundle context.
     * This will retrieve the service with highest priority if there are multiple instances of the service.
     * Based on the timeout parameter representing the max wait time for the service, the lookup for the service will
     * be performed multiple times in one second intervals. The lookup will be performed at least once.
     * @param bundleContext the bundleContext which will be used for service retrieval
     * @param clazz the class of the service to be retrieved
     * @param timeout the max time that will be spent waiting for the service, in miliseconds
     * @param <T> the type of the service to be returned
     * @return the service found or {@code null} if there is no such service in the bundle context
     */
    public static <T> T findService(BundleContext bundleContext, Class<T> clazz, long timeout) {
        return findService(bundleContext, clazz.getName(), timeout);
    }

    /**
     * Retrieves the service of the given class from the bundle context.
     * This will retrieve the service with highest priority if there are multiple instances of the service.
     * Based on the timeout parameter representing the max wait time for the service, the lookup for the service will
     * be performed multiple times in one second intervals. The lookup will be performed at least once.
     * @param bundleContext the bundleContext which will be used for service retrieval
     * @param className the class name of the service to be retrieved
     * @param timeout the max time that will be spent waiting for the service, in miliseconds
     * @param <T> the type of the service to be returned
     * @return the service found or {@code null} if there is no such service in the bundle context
     */
    public static <T> T findService(BundleContext bundleContext, String className, long timeout) {
        long retries = timeout / WAIT_STEP;
        if (retries == 0) {
            retries = 1;
        }

        for (int i = 0; i < retries; i++) {
            T service = findService(bundleContext, className);
            if (service != null) {
                return service;
            } else {
                try {
                    Thread.sleep(WAIT_STEP);
                } catch (InterruptedException e) {
                    throw new ServiceWaitInterruptedException(className, e);
                }
            }
        }

        return null;
    }

    private OSGiServiceUtils() {
    }
}
