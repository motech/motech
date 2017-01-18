package org.motechproject.tasks.service.impl;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tasks.service.DynamicChannelProvider;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;
import org.motechproject.tasks.service.DynamicChannelLoader;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of the {@link DynamicChannelLoader} interface.
 */
@Service
public class DynamicChannelLoaderImpl implements DynamicChannelLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicChannelLoaderImpl.class);

    private static final String PROVIDER_NOT_FOUND = "Dynamic channel provider not found for module {}";

    private BundleContext bundleContext;

    @Autowired
    public DynamicChannelLoaderImpl(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    @Transactional
    public List<TriggerEvent> getDynamicTriggers(String moduleName, int page, int pageSize) {
        LOGGER.debug("Retrieving dynamic triggers for channel {}", moduleName);

        DynamicChannelProvider provider = getChannelProvider(moduleName);

        if (provider != null) {
            return provider.getTriggers(page, pageSize);
        } else {
            LOGGER.debug(PROVIDER_NOT_FOUND, moduleName);
        }

        return new ArrayList<>();
    }

    @Override
    public boolean providesDynamicTriggers(String moduleName) {
        LOGGER.debug("Retrieving dynamic triggers for channel {}", moduleName);

        DynamicChannelProvider provider = getChannelProvider(moduleName);

        return provider != null;
    }

    @Override
    public Long countByChannelModuleName(String moduleName) {
        DynamicChannelProvider provider = getChannelProvider(moduleName);

        if (provider != null) {
            return provider.countTriggers();
        } else {
            LOGGER.debug(PROVIDER_NOT_FOUND, moduleName);
        }

        return null;
    }

    @Override
    @Transactional
    public TriggerEvent getTrigger(TaskTriggerInformation triggerInformation) {
        DynamicChannelProvider provider = getChannelProvider(triggerInformation.getModuleName());

        if (provider != null) {
            return provider.getTrigger(triggerInformation);
        } else {
            LOGGER.debug(PROVIDER_NOT_FOUND, triggerInformation.getModuleName());
        }

        return null;
    }

    @Override
    public boolean channelExists(String moduleName) {
        return getChannelProvider(moduleName) != null;
    }

    @Override
    public boolean validateTrigger(String moduleName, String subject) {
        DynamicChannelProvider provider = getChannelProvider(moduleName);
        return provider.validateSubject(subject);
    }

    private DynamicChannelProvider getChannelProvider(String channelModule) {
        DynamicChannelProvider provider = null;

        try {
            ServiceReference[] allRefs = bundleContext.getServiceReferences(DynamicChannelProvider.class.getName(), null);

            ServiceReference correctRef = findRefBySymbolicName(allRefs, channelModule);

            if (correctRef != null) {
                Object service = bundleContext.getService(correctRef);
                if (service instanceof DynamicChannelProvider) {
                    provider = (DynamicChannelProvider) service;
                    LOGGER.debug("Retrieved channel provider {} for channel {}", provider.getClass().getName(),
                            channelModule);
                } else {
                    LOGGER.warn("Channel provider {} for channel {} is invalid, it is not an instance of {}",
                            service.getClass().getName(), channelModule, DynamicChannelProvider.class.getName());
                }
            } else {
                LOGGER.debug("No channel provider available for channel {}", channelModule);
            }
        } catch (InvalidSyntaxException e) {
            LOGGER.error("Error while retrieving provider references for channel {}", channelModule, e);
        }

        return provider;
    }

    private ServiceReference findRefBySymbolicName(ServiceReference[] refs, String symbolicName) {
        if (refs != null) {
            for (ServiceReference ref : refs) {
                if (StringUtils.equals(symbolicName, ref.getBundle().getSymbolicName())) {
                    return ref;
                }
            }
        }
        return null;
    }
}
