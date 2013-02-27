package org.motechproject.event.osgi;

import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.annotations.EventAnnotationBeanPostProcessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlueprintApplicationContextTracker extends ServiceTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlueprintApplicationContextTracker.class);

    private List<String> contextsProcessed = Collections.synchronizedList(new ArrayList<String>());
    private EventAnnotationBeanPostProcessor eventAnnotationBeanPostProcessor;

    public BlueprintApplicationContextTracker(BundleContext bundleContext, EventListenerRegistryService listenerRegistryService) {
        super(bundleContext, ApplicationContext.class.getName(), null);
        this.eventAnnotationBeanPostProcessor = new EventAnnotationBeanPostProcessor(listenerRegistryService);
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        ApplicationContext applicationContext = (ApplicationContext) super.addingService(serviceReference);
        LOGGER.debug("Staring to process " + applicationContext.getDisplayName());

        if (!new ApplicationContextServiceReference(serviceReference).isValid()) {
            return applicationContext;
        }

        if (contextsProcessed.contains(applicationContext.getId())) {
            return applicationContext;
        }

        contextsProcessed.add(applicationContext.getId());
        eventAnnotationBeanPostProcessor.processAnnotations(applicationContext);

        LOGGER.debug("Processed " + applicationContext.getDisplayName());

        return applicationContext;
    }


    @Override
    public void removedService(ServiceReference reference, Object service) {
        super.removedService(reference, service);
        ApplicationContext applicationContext = (ApplicationContext) service;

        if (new ApplicationContextServiceReference(reference).isValid()) {
            eventAnnotationBeanPostProcessor.clearListeners(applicationContext);
            contextsProcessed.remove(applicationContext.getId());
        }
    }


}
