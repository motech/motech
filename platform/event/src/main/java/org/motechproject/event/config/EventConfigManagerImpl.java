package org.motechproject.event.config;

import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.api.Tenant;
import org.motechproject.config.service.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component("eventConfigManager")
public class EventConfigManagerImpl implements EventConfigManager {

    private static final String QUEUE_FOR_EVENTS = "jms.queue.for.events";
    private static final String QUEUE_FOR_SCHEDULER = "jms.queue.for.scheduler";

    private static final Logger LOG = LoggerFactory.getLogger(EventConfigManagerImpl.class);

    @Autowired
    private ConfigurationService configurationService;

    public EventConfigManagerImpl() {
    }

    public Properties getActivemqConfig() {
        Properties activemqConfig = null;
        if (configurationService != null) {
            activemqConfig = configurationService.getPlatformSettings().getActivemqProperties();
        }

        if (activemqConfig == null || activemqConfig.isEmpty()) {
            LOG.info("Cannot read activemq config from database, loading from classpath");
            activemqConfig = loadFromClasspath("motech-settings.conf");
        }

        if (activemqConfig == null || activemqConfig.isEmpty()) {
            throw new MotechException("Unable to load activemq configuration");
        }

        replaceQueueNames(activemqConfig);

        return activemqConfig;
    }

    private Properties loadFromClasspath(String configFileName) {
        Properties result = null;

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (in != null) {
                Properties activemqConfig = new Properties();
                activemqConfig.load(in);

                result = activemqConfig;
            }
        } catch (IOException e) {
            LOG.error("Error while loading activemq configuration from classpath", e);
        }

        return result;
    }

    private void replaceQueueNames(Properties activeMqConfig) {
        String queuePrefix = getQueuePrefix();

        String queueForEvents = activeMqConfig.getProperty(QUEUE_FOR_EVENTS);

        if (StringUtils.isNotBlank(queueForEvents)) {
            activeMqConfig.setProperty(QUEUE_FOR_EVENTS, queuePrefix + queueForEvents);
        }

        String queueForScheduler = activeMqConfig.getProperty(QUEUE_FOR_SCHEDULER);

        if (StringUtils.isNotBlank(queueForScheduler)) {
            activeMqConfig.setProperty(QUEUE_FOR_SCHEDULER, queuePrefix + queueForScheduler);
        }
    }

    protected String getQueuePrefix() {
        return Tenant.current().getSuffixedId();
    }
}
