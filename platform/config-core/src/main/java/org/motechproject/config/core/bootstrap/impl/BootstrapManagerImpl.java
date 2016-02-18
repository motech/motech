package org.motechproject.config.core.bootstrap.impl;

import org.apache.commons.lang.StringUtils;
import org.motechproject.config.core.exception.MotechConfigurationException;
import org.motechproject.config.core.bootstrap.BootstrapManager;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.SQLDBConfig;
import org.motechproject.config.core.environment.Environment;
import org.motechproject.config.core.filestore.ConfigLocationFileStore;
import org.motechproject.config.core.filestore.ConfigPropertiesUtils;
import org.motechproject.config.core.service.impl.mapper.BootstrapConfigPropertyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * The default implementation for managing the bootstrap configuration.
 */
@Component
public class BootstrapManagerImpl implements BootstrapManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BootstrapManagerImpl.class);

    private Environment environment;

    private ConfigLocationFileStore configLocationFileStore;

    private static final String QUEUE_FOR_EVENTS = "jms.queue.for.events";
    private static final String QUEUE_FOR_SCHEDULER = "jms.queue.for.scheduler";
    private static final String TOPIC_FOR_EVENTS = "jms.topic.for.events";

    @Autowired
    public BootstrapManagerImpl(ConfigLocationFileStore configLocationFileStore, Environment environment) {
        this.configLocationFileStore = configLocationFileStore;
        this.environment = environment;
    }

    @Override
    public BootstrapConfig loadBootstrapConfig() {
        String configLocation = environment.getConfigDir();

        if (StringUtils.isNotBlank(configLocation)) {
            final String errorMessage = String.format("specified by '%s' environment variable.", Environment.MOTECH_CONFIG_DIR);
            return readBootstrapConfigFromFile(new File(getConfigFile(configLocation)), errorMessage);
        }

        try {
            return readBootstrapConfigFromEnvironment();
        } catch (MotechConfigurationException e) {
            LOGGER.debug("Could not find bootstrap configuration values from environment variables. So, trying to load " +
                    "from default location.", e);
            return readBootstrapConfigFromDefaultLocation();
        }
    }

    @Override
    public void saveBootstrapConfig(BootstrapConfig bootstrapConfig) {
        File defaultBootstrapFile = ConfigPropertiesUtils.getDefaultPropertiesFile(ConfigLocation.FileAccessType.WRITABLE,
                configLocationFileStore.getAll(), BOOTSTRAP_PROPERTIES);
        ConfigPropertiesUtils.saveConfig(defaultBootstrapFile, BootstrapConfigPropertyMapper.toProperties(bootstrapConfig));
    }

    @Override
    public Properties getActiveMqConfig() {
        BootstrapConfig bootstrapConfig = loadBootstrapConfig();

        Properties activeMqProperties = bootstrapConfig.getActiveMqProperties();

        if (activeMqProperties == null) {
            return new Properties();
        }

        replaceQueueNames(activeMqProperties);

        return activeMqProperties;
    }

    private void replaceQueueNames(Properties activeMqConfig) {

        String queueForEvents = activeMqConfig.getProperty(QUEUE_FOR_EVENTS);
        if (StringUtils.isNotBlank(queueForEvents)) {
            activeMqConfig.setProperty(QUEUE_FOR_EVENTS, queueForEvents);
        }

        String queueForScheduler = activeMqConfig.getProperty(QUEUE_FOR_SCHEDULER);
        if (StringUtils.isNotBlank(queueForScheduler)) {
            activeMqConfig.setProperty(QUEUE_FOR_SCHEDULER, queueForScheduler);
        }

        String topicForEvents = activeMqConfig.getProperty(TOPIC_FOR_EVENTS);
        if (StringUtils.isNotBlank(topicForEvents)) {
            activeMqConfig.setProperty(TOPIC_FOR_EVENTS, topicForEvents);
        }
    }

    private BootstrapConfig readBootstrapConfigFromDefaultLocation() {
        File bootstrapFile = ConfigPropertiesUtils.getDefaultPropertiesFile(ConfigLocation.FileAccessType.READABLE,
                    configLocationFileStore.getAll(), BOOTSTRAP_PROPERTIES);
        return readBootstrapConfigFromFile(bootstrapFile, StringUtils.EMPTY);
    }

    private String getConfigFile(String configLocation) {
        return configLocation + File.separator + BOOTSTRAP_PROPERTIES;
    }

    private BootstrapConfig readBootstrapConfigFromEnvironment() {
        Properties bootstrapProperties = environment.getBootstrapProperties();
        String sqlUrl = bootstrapProperties.getProperty(BootstrapConfig.SQL_URL);
        String sqlUsername = bootstrapProperties.getProperty(BootstrapConfig.SQL_USER);
        String sqlPassword = bootstrapProperties.getProperty(BootstrapConfig.SQL_PASSWORD);
        String configSource = bootstrapProperties.getProperty(BootstrapConfig.CONFIG_SOURCE);
        String sqlDriver = bootstrapProperties.getProperty(BootstrapConfig.SQL_DRIVER);
        String osgiStorage = bootstrapProperties.getProperty(BootstrapConfig.OSGI_FRAMEWORK_STORAGE);
        String queueURL = bootstrapProperties.getProperty(BootstrapConfig.QUEUE_URL);

        Properties activeMqProperties = environment.getActiveMqProperties();
        return new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, sqlUsername, sqlPassword), ConfigSource.valueOf(configSource), osgiStorage, queueURL, activeMqProperties);
    }

    private BootstrapConfig readBootstrapConfigFromFile(File configFile, String errorMessage) {
        try {
            LOGGER.debug("Trying to load bootstrap configuration from " + configFile.getAbsolutePath());

            Properties properties = ConfigPropertiesUtils.getPropertiesFromFile(configFile);
            return BootstrapConfigPropertyMapper.fromProperties(properties);
        } catch (IOException e) {
            throw new MotechConfigurationException("Error loading bootstrap properties from config file " + configFile + " " + errorMessage, e);
        }
    }
}
