package org.motechproject.event.osgi;

import org.motechproject.commons.api.ApplicationContextServiceReferenceUtils;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.annotations.EventAnnotationBeanPostProcessor;
import org.motechproject.osgi.web.ApplicationContextTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class BlueprintApplicationContextTracker extends ApplicationContextTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlueprintApplicationContextTracker.class);

    private EventAnnotationBeanPostProcessor eventAnnotationBeanPostProcessor;

    public BlueprintApplicationContextTracker(BundleContext bundleContext, EventListenerRegistryService listenerRegistryService) {
        super(bundleContext);
        this.eventAnnotationBeanPostProcessor = new EventAnnotationBeanPostProcessor(listenerRegistryService);
    }

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
