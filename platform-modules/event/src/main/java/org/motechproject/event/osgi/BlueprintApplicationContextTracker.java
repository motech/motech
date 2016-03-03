package org.motechproject.event.osgi;

import org.motechproject.commons.api.ApplicationContextServiceReferenceUtils;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.proxy.EventAnnotationBeanPostProcessor;
import org.motechproject.osgi.web.tracker.ApplicationContextTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Tracks the application contexts that are published as OSGi services. These contexts are then scanned for beans annotated
 * as event listeners and registers them in the registry.
 */
public class BlueprintApplicationContextTracker extends ApplicationContextTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlueprintApplicationContextTracker.class);

    private EventAnnotationBeanPostProcessor eventAnnotationBeanPostProcessor;

    /**
     *
     * @param bundleContext the bundle's execution context within the Framework
     * @param listenerRegistryService the service for event listeners.
     */
    public BlueprintApplicationContextTracker(BundleContext bundleContext, EventListenerRegistryService listenerRegistryService) {
        super(bundleContext);
        this.eventAnnotationBeanPostProcessor = new EventAnnotationBeanPostProcessor(listenerRegistryService);
    }

    /**
     * {@inheritDoc}. Additionally it processes event annotations.
     */
    @Override
    public Object addingService(ServiceReference serviceReference) {
        ApplicationContext applicationContext = (ApplicationContext) super.addingService(serviceReference);
        LOGGER.debug("Staring to process " + applicationContext.getDisplayName());

        synchronized (getLock()) {
            if (contextInvalidOrProcessed(serviceReference, applicationContext)) {
                return applicationContext;
            }
            markAsProcessed(applicationContext);
        }

        eventAnnotationBeanPostProcessor.processAnnotations(applicationContext);

        LOGGER.debug("Processed " + applicationContext.getDisplayName());

        return applicationContext;
    }

    /**
     * {@inheritDoc}. Additionally it removes all event listeners registered
     * in the <code>ApplicationContext</code>
     */
    @Override
    public void removedService(ServiceReference reference, Object service) {
        super.removedService(reference, service);
        ApplicationContext applicationContext = (ApplicationContext) service;

        if (ApplicationContextServiceReferenceUtils.isValid(reference)) {
            eventAnnotationBeanPostProcessor.clearListeners(applicationContext);
            synchronized (getLock()) {
                removeFromProcessed(applicationContext);
            }
        }
    }
}
