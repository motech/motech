package org.motechproject.config.core.datanucleus.impl;

import org.apache.commons.lang.StringUtils;
import org.motechproject.config.core.exception.MotechConfigurationException;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.core.datanucleus.DbConfigManager;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.environment.Environment;
import org.motechproject.config.core.filestore.ConfigLocationFileStore;
import org.motechproject.config.core.filestore.ConfigPropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Implementation of the {@link DbConfigManager}
 * Component to loading datanucleus properties
 */
@Component
public class DbConfigManagerImpl implements DbConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbConfigManagerImpl.class);

    private static final String DATANUCLEUS = "DataNucleus";
    private static final String FLYWAY = "Flyway";

    private Environment environment;

    private ConfigLocationFileStore configLocationFileStore;

    @Override
    public Properties getDatanucleusDataProperties() {
        return getProperties(ConfigurationConstants.DATANUCLEUS_DATA_SETTINGS_FILE_NAME,
                Environment.MOTECH_DATANUCLEUS_DATA_ROPERTIES, DATANUCLEUS);
    }

    @Override
    public Properties getDatanucleusSchemaProperties() {
        return getProperties(ConfigurationConstants.DATANUCLEUS_SCHEMA_SETTINGS_FILE_NAME,
                Environment.MOTECH_DATANUCLEUS_SCHEMA_PROPERTIES, DATANUCLEUS);
    }

    @Override
    public Properties getDatanucleusQuartzProperties() {
        return getProperties(ConfigurationConstants.DATANUCLEUS_QUARTZ_SETTINGS_FILE_NAME,
                Environment.MOTECH_DATANUCLEUS_QUARTZ_PROPERTIES, DATANUCLEUS);
    }

    @Override
    public Properties getFlywayDataProperties() {
        return getProperties(ConfigurationConstants.FLYWAY_DATA_SETTINGS_FILE_NAME,
                Environment.MOTECH_FLYWAY_DATA_PROPERTIES, FLYWAY);
    }

    @Override
    public Properties getFlywaySchemaProperties() {
        return getProperties(ConfigurationConstants.FLYWAY_SCHEMA_SETTINGS_FILE_NAME,
                Environment.MOTECH_FLYWAY_SCHEMA_PROPERTIES, FLYWAY);
    }

    private Properties getProperties(String fileName, String varName, String descForLogs) {
        String configLocation = environment.getConfigDir();
        Properties properties;

        if (StringUtils.isNotBlank(configLocation)) {
            return readPropertiesFromFile(new File(configLocation, fileName), descForLogs);
        }

        properties = environment.getProperties(varName);

        if (properties != null && !properties.isEmpty()) {
            return properties;
        }
        LOGGER.debug("Could not find all {} configuration values from environment variables. So, trying to load from classpath", descForLogs);

        try {
            LOGGER.debug("Loading {} properties from default configuration directory", descForLogs);
            properties = loadPropertiesFromDefaultLocation(fileName, descForLogs);
        } catch (MotechConfigurationException e) {
            LOGGER.warn(e.getMessage());
        }

        LOGGER.debug("Loading {} properties from classpath", descForLogs);
        if (properties == null || properties.isEmpty()) {
            properties = loadPropertiesFromClasspath(fileName, descForLogs);
        }

        return properties;
    }

    private Properties loadPropertiesFromDefaultLocation(String fileName, String descForLogs) {
        File file = ConfigPropertiesUtils.getDefaultPropertiesFile(ConfigLocation.FileAccessType.READABLE,
                configLocationFileStore.getAll(), fileName);

        if (!file.exists()) {
            return null;
        }

        try {
            return ConfigPropertiesUtils.getPropertiesFromFile(file);
        } catch (IOException e) {
            throw new MotechConfigurationException(String.format("Error loading %s properties from %s", descForLogs, file.getAbsolutePath()), e);
        }

    }

    private Properties loadPropertiesFromClasspath(String fileName, String descForLogs) {
        Properties properties = new Properties();
        ClassPathResource resource = new ClassPathResource(fileName);
        try (InputStream inputStream = resource.getInputStream()) {
            properties.load(inputStream);
            // After loading properties from classpath we copy file to the default config location
            File file = ConfigPropertiesUtils.getDefaultPropertiesFile(ConfigLocation.FileAccessType.WRITABLE,
                    configLocationFileStore.getAll(), fileName);
            ConfigPropertiesUtils.saveConfig(file, properties);
        } catch (IOException e) {
            LOGGER.warn("Error occurred when loading {} properties from classpath", descForLogs, e);
        }
        return properties;
    }

    private Properties readPropertiesFromFile(File file, String descForLogs) {
        try {
            return ConfigPropertiesUtils.getPropertiesFromFile(file);
        } catch (IOException e) {
            throw new MotechConfigurationException(String.format("Error loading %s properties from configuration directory specified by '%s' environment variable",
                    descForLogs, Environment.MOTECH_CONFIG_DIR), e);
        }
    }

    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Autowired
    public void setConfigLocationFileStore(ConfigLocationFileStore configLocationFileStore) {
        this.configLocationFileStore = configLocationFileStore;
    }
}
