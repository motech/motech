package org.motechproject.config.core.bootstrap.impl;

import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.api.Tenant;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.bootstrap.BootstrapManager;
import org.motechproject.config.core.bootstrap.Environment;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.SQLDBConfig;
import org.motechproject.config.core.filestore.ConfigLocationFileStore;
import org.motechproject.config.core.filestore.PropertiesReader;
import org.motechproject.config.core.service.impl.mapper.BootstrapConfigPropertyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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

    @Autowired
    public BootstrapManagerImpl(ConfigLocationFileStore configLocationFileStore, Environment environment) {
        this.configLocationFileStore = configLocationFileStore;
        this.environment = environment;
    }

    @Override
    public BootstrapConfig loadBootstrapConfig() {
        String configLocation = environment.getConfigDir();

        if (configLocation != null) {
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
        File defaultBootstrapFile = getDefaultBootstrapFile(ConfigLocation.FileAccessType.WRITABLE);
        try {
            defaultBootstrapFile.getParentFile().mkdirs();
            defaultBootstrapFile.createNewFile();

            Properties bootstrapProperties = BootstrapConfigPropertyMapper.toProperties(bootstrapConfig);

            try (Writer writer = new FileWriter(defaultBootstrapFile)) {
                bootstrapProperties.store(writer, "MOTECH bootstrap properties.");
            }
        } catch (IOException e) {
            String errorMessage = "Error saving bootstrap properties to file";
            LOGGER.error(errorMessage + " " + e.getMessage());
            throw new MotechConfigurationException(errorMessage, e);
        }
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

    private String getQueuePrefix() {
        return Tenant.current().getSuffixedId();
    }

    private File getDefaultBootstrapFile(ConfigLocation.FileAccessType accessType) {
        Iterable<ConfigLocation> configLocations = configLocationFileStore.getAll();
        StringBuilder sb = new StringBuilder("");
        for (ConfigLocation configLocation : configLocations) {
            sb.append(configLocation.getLocation()).append(' ');
            try {
                return configLocation.getFile(BOOTSTRAP_PROPERTIES, accessType);
            } catch (MotechConfigurationException e) {
                LOGGER.warn(e.getMessage());
            }
        }

        throw new MotechConfigurationException(String.format("%s file is not %s from any of the default locations. Searched directories: %s.", BOOTSTRAP_PROPERTIES, accessType.toString(), sb));
    }

    private BootstrapConfig readBootstrapConfigFromDefaultLocation() {
        File bootstrapFile;
        try {
            bootstrapFile = getDefaultBootstrapFile(ConfigLocation.FileAccessType.READABLE);
        } catch (MotechConfigurationException ex) {
            LOGGER.warn(ex.getMessage());
            throw ex;
        }

        return readBootstrapConfigFromFile(bootstrapFile, StringUtils.EMPTY);
    }

    private String getConfigFile(String configLocation) {
        return configLocation + File.separator + BOOTSTRAP_PROPERTIES;
    }

    private BootstrapConfig readBootstrapConfigFromEnvironment() {
        String sqlUrl = environment.getSqlUrl();
        String sqlUsername = environment.getSqlUsername();
        String sqlPassword = environment.getSqlPassword();
        String tenantId = environment.getTenantId();
        String configSource = environment.getConfigSource();
        String sqlDriver = environment.getSqlDriver();
        String osgiStorage = environment.getOsgiFrameworkStorage();
        String queueURL = environment.getQueueUrl();
        Properties activeMqProperties = PropertiesReader.getPropertiesFromString(environment.getActiveMqProperties());

        return new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, sqlUsername, sqlPassword), tenantId, ConfigSource.valueOf(configSource), osgiStorage, queueURL, activeMqProperties);
    }

    private BootstrapConfig readBootstrapConfigFromFile(File configFile, String errorMessage) {
        try {
            LOGGER.debug("Trying to load bootstrap configuration from " + configFile.getAbsolutePath());

            Properties properties = PropertiesReader.getPropertiesFromFile(configFile);
            return BootstrapConfigPropertyMapper.fromProperties(properties);
        } catch (IOException e) {
            final String message = "Error loading bootstrap properties from config file " + configFile + " " + errorMessage;
            LOGGER.warn(message);
            throw new MotechConfigurationException(message, e);
        }
    }
}
