package org.motechproject.tasks.osgi;

import org.motechproject.commons.api.ApplicationContextServiceReferenceUtils;
import org.motechproject.tasks.annotations.TaskAnnotationBeanPostProcessor;
import org.motechproject.tasks.service.ChannelService;
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

        Resource resource = applicationContext.getResource("classpath:task-channel.json");
        if (resource != null && resource.exists()) {
            try {
                channelService.registerChannel(resource.getInputStream());
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        taskAnnotationBeanPostProcessor.processAnnotations(applicationContext);

        LOGGER.debug("Processed " + applicationContext.getDisplayName());

        return applicationContext;
    }

}
