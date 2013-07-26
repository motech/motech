package org.motechproject.tasks.osgi;

import org.motechproject.commons.api.ApplicationContextServiceReferenceUtils;
import org.motechproject.tasks.annotations.TaskAnnotationBeanPostProcessor;
import org.motechproject.tasks.ex.ValidationException;
import org.motechproject.tasks.service.ChannelService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

/**
 * This is effectively a bundle start/stop listener that registers/deregisters a bundle's task channel when a bundle is started/stopped respectively.
 */
public class BlueprintApplicationContextTracker extends ServiceTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlueprintApplicationContextTracker.class);

    private final List<String> contextsProcessed = Collections.synchronizedList(new ArrayList<String>());
    private ChannelService channelService;
    private TaskAnnotationBeanPostProcessor taskAnnotationBeanPostProcessor;

    public BlueprintApplicationContextTracker(BundleContext bundleContext, ChannelService channelService) {
        super(bundleContext, ApplicationContext.class.getName(), null);
        this.channelService = channelService;
        this.taskAnnotationBeanPostProcessor = new TaskAnnotationBeanPostProcessor(bundleContext, channelService);
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        ApplicationContext applicationContext = (ApplicationContext) super.addingService(serviceReference);
        LOGGER.debug("Staring to process " + applicationContext.getDisplayName());

        if (ApplicationContextServiceReferenceUtils.isNotValid(serviceReference)) {
            return applicationContext;
        }

        synchronized (contextsProcessed) {
            if (contextsProcessed.contains(applicationContext.getId())) {
                return applicationContext;
            }
            contextsProcessed.add(applicationContext.getId());
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
    }
}
