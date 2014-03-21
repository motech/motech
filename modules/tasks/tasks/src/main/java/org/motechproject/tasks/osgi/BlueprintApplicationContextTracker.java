package org.motechproject.tasks.osgi;

import org.motechproject.osgi.web.ApplicationContextTracker;
import org.motechproject.tasks.annotations.TaskAnnotationBeanPostProcessor;
import org.motechproject.tasks.ex.ValidationException;
import org.motechproject.tasks.service.ChannelService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static java.lang.String.format;

/**
 * This is effectively a bundle start/stop listener that registers/deregisters a bundle's task channel when a bundle is started/stopped respectively.
 */
public class BlueprintApplicationContextTracker extends ApplicationContextTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlueprintApplicationContextTracker.class);

    private ChannelService channelService;
    private TaskAnnotationBeanPostProcessor taskAnnotationBeanPostProcessor;

    public BlueprintApplicationContextTracker(BundleContext bundleContext, ChannelService channelService) {
        super(bundleContext);
        this.channelService = channelService;
        this.taskAnnotationBeanPostProcessor = new TaskAnnotationBeanPostProcessor(bundleContext, channelService);
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

        try {
            Resource resource = applicationContext.getResource("classpath:task-channel.json");
            if (resource != null && resource.exists()) {
                Bundle module = serviceReference.getBundle();
                channelService.registerChannel(resource.getInputStream(), module.getSymbolicName(), module.getVersion().toString());
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(format("Registered channel for %s successfully.", module.getSymbolicName()));
                }
            }
        } catch (IOException | ValidationException e) {
            LOGGER.error(e.getMessage(), e);
        }

        try {
            taskAnnotationBeanPostProcessor.processAnnotations(applicationContext);
        } catch (ValidationException e) {
            LOGGER.error(e.getMessage(), e);
        }

        LOGGER.debug("Processed " + applicationContext.getDisplayName());
        return applicationContext;
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        super.removedService(reference, service);

        Bundle module = reference.getBundle();
        channelService.deregisterChannel(module.getSymbolicName());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(format("Deregistered channel for %s.", module.getSymbolicName()));
        }

        synchronized (getLock()) {
            removeFromProcessed((ApplicationContext) service);
        }
    }
}
