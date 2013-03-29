package org.motechproject.event.config;

import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.api.TenantIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component("eventConfigManager")
public class EventConfigManagerImpl implements EventConfigManager {

    private static final String QUEUE_FOR_EVENTS = "queue.for.events";
    private static final String QUEUE_FOR_SCHEDULER = "queue.for.scheduler";

    private static final Logger LOG = LoggerFactory.getLogger(EventConfigManagerImpl.class);

    private String activemqConfigLocation;

    public EventConfigManagerImpl() {
        activemqConfigLocation = String.format("%s/.motech/config/activemq.properties",
                System.getProperty("user.home"));
    }

    public String getActivemqConfigLocation() {
        return activemqConfigLocation;
    }

    public void setActivemqConfigLocation(String activemqConfigFileLocation) {
        this.activemqConfigLocation = activemqConfigFileLocation;
    }

    public Properties getActivemqConfig() {
        Properties activemqConfig = loadFromFs(activemqConfigLocation);

        if (activemqConfig == null) {
            LOG.info("Cannot read activemq config from " + activemqConfigLocation + ", loading from classpath");
            activemqConfig = loadFromClasspath("activemq.properties");
        }

        if (activemqConfig == null) {
            throw new MotechException("Unable to load activemq configuration");
        }

        replaceQueueNames(activemqConfig);

        return activemqConfig;
    }

    private Properties loadFromFs(String configFileLocation) {
        Properties result = null;

        File configFile = new File(configFileLocation);

        if (configFile.exists()) {
            try (InputStream in = new FileInputStream(configFile)) {
                Properties activemqConfig = new Properties();
                activemqConfig.load(in);

                result = activemqConfig;
            } catch (IOException e) {
                LOG.error("Error while loading activemq configuration from " + configFileLocation, e);
            }
        }

        return result;
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
        return TenantIdentity.getTenantId() + "_";
    }
}
