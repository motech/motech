package org.motechproject.osgi.web;

import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.motechproject.commons.api.MotechException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class BundleContextWrapper implements BundleContextAware {
    private BundleContext bundleContext;

    public static final String CONTEXT_SERVICE_NAME = "org.springframework.context.service.name";

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleContextWrapper.class);

    public BundleContextWrapper() {
    }

    public BundleContextWrapper(BundleContext context) {
        this.bundleContext = context;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public <T> T getService(Class<T> clazz) {
        ServiceReference serviceReference = bundleContext.getServiceReference(clazz.getName());
        if (serviceReference == null) {
            return null;
        }
        return (T) bundleContext.getService(serviceReference);
    }

    public ApplicationContext getBundleApplicationContext() {
        ApplicationContext applicationContext = null;
        String filter = String.format("(%s=%s)", CONTEXT_SERVICE_NAME, getCurrentBundleSymbolicName());
        ServiceReference[] serviceReferences;
        try {
            serviceReferences = bundleContext.getServiceReferences(ApplicationContext.class.getName(), filter);
        } catch (InvalidSyntaxException e) {
            throw new MotechException(e.getMessage(), e);
        }
        if (serviceReferences != null) {
            applicationContext = (ApplicationContext) bundleContext.getService(serviceReferences[0]);
        }
        if (applicationContext != null) {
            LOGGER.debug("Application context is " + applicationContext.getDisplayName());
        }
        return applicationContext;
    }

    public String getCurrentBundleSymbolicName() {
        Bundle bundle = bundleContext.getBundle();
        return bundle.getSymbolicName();
    }

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

}
