package org.motechproject.testing.osgi.helper;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * A utility class for retrieving services and web application contexts(published as services)
 * from the bundle context. The retrieval is done with retries, their length or duration can be specified.
 */
public final class ServiceRetriever {

    public static final int DEFAULT_WAIT_TIME = 500;
    public static final int DEFAULT_RETRIES = 100;

    /**
     * Retrieves the Spring web application context registered by the bundle to which the provided bundle
     * context belongs to. If there is no matching context, this method will keep retrying 100 times every half a second.
     * If this method is unable to retrieve the context during that time, it will fail the test.
     * The context is retrieved using the bundle context, since Spring contexts are registered as OSGi services
     * by the Gemini extender.
     * @param bundleContext the bundle context of the bundle for which the context should be retrieved
     * @return the context for the matching bundle, never null
     */
    public static WebApplicationContext getWebAppContext(BundleContext bundleContext) {
        return getWebAppContext(bundleContext, bundleContext.getBundle().getSymbolicName());
    }

    /**
     * Retrieves the web application context registered by the bundle with the given symbolic name.
     * If there is no matching context, this method will keep retrying 100 times every half a second.
     * If this method is unable to retrieve the context during that time, it will fail the test.
     * The context is retrieved using the bundle context, since Spring contexts are registered as OSGi services
     * by the Gemini extender.
     * @param bundleContext the bundle context used for retrieving the context
     * @param bundleSymbolicName the symbolic name of the bundle for which the context should get retrieved
     * @return the context for the matching bundle, never null
     */
    public static WebApplicationContext getWebAppContext(BundleContext bundleContext, String bundleSymbolicName) {
        return getWebAppContext(bundleContext, bundleSymbolicName, DEFAULT_WAIT_TIME, DEFAULT_RETRIES);
    }

    /**
     * Retrieves the web application context registered by the bundle with the given symbolic name.
     * If there is no matching context, this method will keep retrying the given amount of times, using the provided
     * wait interval. If this method is unable to retrieve the context during that time, it will fail the test.
     * The context is retrieved using the bundle context, since Spring contexts are registered as OSGi services
     * by the Gemini extender.
     * @param bundleContext the bundle context used for retrieving the context
     * @param bundleSymbolicName the symbolic name of the bundle for which the context should get retrieved
     * @param retrievalWaitTime the interval time to wait between attempts to retrieve the context, in milliseconds
     * @param retrievalRetries the maximum number of attempts that will be made to retrieve the context
     * @return the context for the matching bundle, never null
     */
    public static WebApplicationContext getWebAppContext(BundleContext bundleContext, String bundleSymbolicName,
                                                         int retrievalWaitTime, int retrievalRetries)  {
        WebApplicationContext theContext = null;

        int tries = 0;

        try {
            do {
                ServiceReference[] references =
                        bundleContext.getAllServiceReferences(WebApplicationContext.class.getName(), null);

                if (references != null) {
                    for (ServiceReference ref : references) {
                        if (bundleSymbolicName.equals(ref.getBundle().getSymbolicName())) {
                            theContext = (WebApplicationContext) bundleContext.getService(ref);
                            break;
                        }
                    }
                }

                ++tries;
                Thread.sleep(retrievalWaitTime);
            } while (theContext == null && tries < retrievalRetries);
        } catch (InvalidSyntaxException | InterruptedException e) {
            fail("Unable to retrieve web application");
        }

        assertNotNull("Unable to retrieve the bundle context for " + bundleSymbolicName, theContext);

        return theContext;
    }

    /**
     * Retrieves an OSGi service of a given class from the bundle context. This method will keep retrying if the service is
     * not available. It will retry 100 times every 0.5 seconds. If the service is still unavailable it will fail the
     * test.
     * @param bundleContext the bundle context used for retrieving the service reference
     * @param clazz the class of the service that should get retrieved
     * @param <T> the type of the service that should get retrieved
     * @return the OSGi service of the given class, never null
     */
    public static  <T> T getService(BundleContext bundleContext, Class<T> clazz) {
        return getService(bundleContext, clazz, DEFAULT_WAIT_TIME, DEFAULT_RETRIES);
    }

    /**
     * Retrieves an OSGi service of a given class from the bundle context. This method will keep retrying if the service is
     * not available. It will retry 100 times every 0.5 seconds. If the service is still unavailable it will fail the
     * test.
     * @param bundleContext the bundle context used for retrieving the service reference
     * @param className the name of the class of the service that should get retrieved
     * @return the OSGi service of the given class, never null
     */
    public static Object getService(BundleContext bundleContext, String className) {
        return getService(bundleContext, className, DEFAULT_WAIT_TIME, DEFAULT_RETRIES, false);
    }

    /**
     * Retrieves an OSGi service of a given class from the bundle context. This method will keep retrying if the service is
     * not available. It will retry 100 times every 0.5 seconds. If the service is still unavailable it will fail the
     * test.
     * @param bundleContext the bundle context used for retrieving the service reference
     * @param className the name of the class of the service that should get retrieved
     * @param checkAllReferences whether to retrieve all references for the service and use the first one available
     * @return the OSGi service of the given class, never null
     */
    public static Object getService(BundleContext bundleContext, String className, boolean checkAllReferences) {
        return getService(bundleContext, className, DEFAULT_WAIT_TIME, DEFAULT_RETRIES, checkAllReferences);
    }

    /**
     * Retrieves an OSGi service of a given class from the bundle context. This method will keep retrying if the service is
     * not available. It will retry the given number of times using the given wait interval to wait between each retrieval attempt.
     * If the service is still unavailable it will fail the test.
     * @param bundleContext the bundle context used for retrieving the service reference
     * @param clazz the class of the service that should get retrieved
     * @param retrievalWaitTime the interval time to wait between attempts to retrieve the context, in milliseconds
     * @param retrievalRetries the maximum number of attempts that will be made to retrieve the service
     * @param <T> the type of the service that should get retrieved
     * @return the OSGi service of the given class, never null
     */
    public static  <T> T getService(BundleContext bundleContext, Class<T> clazz,
                                    int retrievalWaitTime, int retrievalRetries) {
        return (T) getService(bundleContext, clazz.getName(), retrievalWaitTime, retrievalRetries, false);
    }

    /**
     * Retrieves an OSGi service of a given class from the bundle context. This method will keep retrying if the service is
     * not available. It will retry the given number of times using the given wait interval to wait between each retrieval attempt.
     * If the service is still unavailable it will fail the test.
     * @param bundleContext the bundle context used for retrieving the service reference
     * @param className the name of the class of the service that should get retrieved
     * @param retrievalWaitTime the interval time to wait between attempts to retrieve the context, in milliseconds
     * @param retrievalRetries the maximum number of attempts that will be made to retrieve the service
     * @param checkAllReferences whether to retrieve all references for the service and use the first one available
     * @return the OSGi service of the given class, never null
     */
    public static Object getService(BundleContext bundleContext, String className,
                                    int retrievalWaitTime, int retrievalRetries, boolean checkAllReferences) {
        Object service = null;

        int tries = 0;

        try {
            do {
                ServiceReference ref = null;
                if (checkAllReferences) {
                    ServiceReference<?>[] allServiceReferences = bundleContext.getAllServiceReferences(className, null);
                    if (allServiceReferences != null) {
                        ref = allServiceReferences[0];
                    }
                } else {
                    ref = bundleContext.getServiceReference(className);
                }
                if (ref != null) {
                    service = bundleContext.getService(ref);
                    break;
                }

                ++tries;
                Thread.sleep(retrievalWaitTime);
            } while (tries < retrievalRetries);
        } catch (InterruptedException | InvalidSyntaxException e) {
            fail("Unable to retrieve service of class " + className);
        }

        assertNotNull("Unable to retrieve the service " + className, service);

        return service;
    }

    private ServiceRetriever() {
    }
}
