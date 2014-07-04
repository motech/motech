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
import java.io.InputStream;

import static org.motechproject.mds.util.Constants.BundleNames.MDS_BUNDLE_SYMBOLIC_NAME;
import static org.motechproject.mds.util.Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME;

/**
 * This is effectively a bundle start/stop listener that registers/deregisters a bundle's task channel when a bundle is started/stopped respectively.
 */
public class TasksBlueprintApplicationContextTracker extends ApplicationContextTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(TasksBlueprintApplicationContextTracker.class);

    private ChannelService channelService;
    private TaskAnnotationBeanPostProcessor taskAnnotationBeanPostProcessor;

    public TasksBlueprintApplicationContextTracker(BundleContext bundleContext, ChannelService channelService) {
        super(bundleContext);

        this.channelService = channelService;
        this.taskAnnotationBeanPostProcessor = new TaskAnnotationBeanPostProcessor(bundleContext, channelService);
    }

    @Override
    public Object addingService(ServiceReference reference) {
        Bundle module = reference.getBundle();
        ApplicationContext applicationContext = (ApplicationContext) super.addingService(reference);

        if (isMDS(module.getSymbolicName())) {
            return applicationContext;
        }

        LOGGER.debug("Staring to process {}", applicationContext.getDisplayName());

        synchronized (getLock()) {
            if (contextInvalidOrProcessed(reference, applicationContext)) {
                return applicationContext;
            }

            markAsProcessed(applicationContext);
        }

        try {
            Resource resource = applicationContext.getResource("classpath:task-channel.json");
            if (resource.exists()) {

                try (InputStream stream = resource.getInputStream()) {
                    channelService.registerChannel(stream, module.getSymbolicName(), module.getVersion().toString());
                }

                LOGGER.info("Registered channel for {} successfully.", module.getSymbolicName());
            }
        } catch (IOException | ValidationException e) {
            LOGGER.error(e.getMessage(), e);
        }

        try {
            taskAnnotationBeanPostProcessor.processAnnotations(applicationContext);
        } catch (ValidationException e) {
            LOGGER.error(e.getMessage(), e);
        }

        LOGGER.debug("Processed {}", applicationContext.getDisplayName());
        return applicationContext;
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        Bundle module = reference.getBundle();
        super.removedService(reference, service);

        if (isMDS(module.getSymbolicName())) {
            return;
        }

        synchronized (getLock()) {
            removeFromProcessed((ApplicationContext) service);
        }
    }

    private boolean isMDS(String symbolicName) {
        return MDS_ENTITIES_SYMBOLIC_NAME.equalsIgnoreCase(symbolicName)
                || MDS_BUNDLE_SYMBOLIC_NAME.equalsIgnoreCase(symbolicName);
    }
}
