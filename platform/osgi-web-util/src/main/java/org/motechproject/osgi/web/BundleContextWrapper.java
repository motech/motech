package org.motechproject.osgi.web;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.motechproject.commons.api.MotechException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * This is a wrapper class for the OSGi {@link org.osgi.framework.BundleContext} class.
 * It provides convenience methods for processing Blueprint contexts of modules.
 * This class implements {@link org.eclipse.gemini.blueprint.context.BundleContextAware}, so if it's published
 * as a Spring bean, the bundle context object should get injected by Spring.
 */
public class BundleContextWrapper implements BundleContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleContextWrapper.class);

    /**
     * The service property set by Blueprint for application contexts published at services.
     * The value for this property will be equal to the symbolic name of the bundle from which the context comes from.
     * It allows to retrieve a published context for a given bundle from the bundle context.
     */
    public static final String CONTEXT_SERVICE_NAME = "org.springframework.context.service.name";

    private BundleContext bundleContext;

    /**
     * The default constructor, expects the bundle context to be injected by Spring.
     */
    public BundleContextWrapper() {
    }

    /**
     * Constructs this wrapper for a given bundle context.
     * @param context the bundle context to wrap around
     */
    public BundleContextWrapper(BundleContext context) {
        this.bundleContext = context;
    }

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    /**
     * @return the underlying {@link org.osgi.framework.BundleContext} object
     */
    public BundleContext getBundleContext() {
        return bundleContext;
    }

    /**
     * Retrieves an OSGi service using the underlying bundle context. Note that this will return the
     * service from the first reference, so multiple services for one class are not supported by this method (you will get a random instance).
     * @param clazz the class of the service to retrieve
     * @param <T> the class of the service to retrieve
     * @return an OSGi service for the class, or null if there is no such service available
     */
    public <T> T getService(Class<T> clazz) {
        ServiceReference serviceReference = bundleContext.getServiceReference(clazz.getName());
        if (serviceReference == null) {
            return null;
        }
        return (T) bundleContext.getService(serviceReference);
    }

    /**
     * Returns the Spring {@link org.springframework.context.ApplicationContext} created by Blueprint for the
     * bundle the underlying bundle context comes from. The context is retrieved from the bundle context, since
     * Blueprint publishes application contexts as OSGi services.
     * @return the context created by Blueprint for the bundle the underlying context comes from, or null if there is no context
     */
    public ApplicationContext getBundleApplicationContext() {
        ApplicationContext applicationContext = null;
        String filter = String.format("(%s=%s)", CONTEXT_SERVICE_NAME, getCurrentBundleSymbolicName());
        ServiceReference[] serviceReferences;
        try {
            serviceReferences = bundleContext.getServiceReferences(ApplicationContext.class.getName(), filter);
        } catch (InvalidSyntaxException e) {
            throw new MotechException(e.getMessage(), e);
        }
        if (ArrayUtils.isNotEmpty(serviceReferences)) {
            applicationContext = (ApplicationContext) bundleContext.getService(serviceReferences[0]);
        }
        if (applicationContext != null) {
            LOGGER.debug("Application context is " + applicationContext.getDisplayName());
        }
        return applicationContext;
    }

    /**
     * @return the symbolic name of the bundle from which the underlying context comes from
     */
    public String getCurrentBundleSymbolicName() {
        Bundle bundle = bundleContext.getBundle();
        return bundle.getSymbolicName();
    }
}
