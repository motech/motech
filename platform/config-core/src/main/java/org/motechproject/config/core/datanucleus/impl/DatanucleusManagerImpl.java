package org.motechproject.config.core.datanucleus.impl;

import org.apache.commons.lang.StringUtils;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.core.datanucleus.DatanucleusManager;
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
 * Implementation of the {@link org.motechproject.config.core.datanucleus.DatanucleusManager}
 * Component to loading datanucleus properties
 */
@Component
public class DatanucleusManagerImpl implements DatanucleusManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatanucleusManagerImpl.class);

    private Environment environment;

    private ConfigLocationFileStore configLocationFileStore;

    @Override
    public Properties getDatanucleusDataProperties() {
        return getDatanucleusProperties(ConfigurationConstants.DATANUCLEUS_DATA_SETTINGS_FILE_NAME);
    }

    @Override
    public Properties getDatanucleusSchemaProperties() {
        return getDatanucleusProperties(ConfigurationConstants.DATANUCLEUS_SCHEMA_SETTINGS_FILE_NAME);
    }

    private Properties getDatanucleusProperties(String fileName) {
        String configLocation = environment.getConfigDir();
        Properties datanucleusProperties;

        if (StringUtils.isNotBlank(configLocation)) {
            return readDatanucleusPropertiesFromFile(new File(configLocation, fileName));
        }

        if (ConfigurationConstants.DATANUCLEUS_DATA_SETTINGS_FILE_NAME.equals(fileName)) {
            datanucleusProperties = environment.getDatanucleusDataProperties();
        } else {
            datanucleusProperties = environment.getDatanucleusSchemaProperties();
        }

        if (datanucleusProperties != null && !datanucleusProperties.isEmpty()) {
            return datanucleusProperties;
        }
        LOGGER.debug("Could not find all datanucleus configuration values from environment variables. So, trying to load from classpath");

        try {
            LOGGER.debug("Loading datanucleus properties from default configuration directory");
            datanucleusProperties = loadPropertiesFromDefaultLocation(fileName);
        } catch(MotechConfigurationException e) {
            LOGGER.warn(e.getMessage());
        }

        LOGGER.debug("Loading datanucleus properties from classpath");
        if (datanucleusProperties == null || datanucleusProperties.isEmpty()) {
            datanucleusProperties = loadPropertiesFromClasspath(fileName);
        }

        return datanucleusProperties;
    }

    private Properties loadPropertiesFromDefaultLocation(String fileName) {
        File file = ConfigPropertiesUtils.getDefaultPropertiesFile(ConfigLocation.FileAccessType.READABLE,
                configLocationFileStore.getAll(), fileName);

        if (!file.exists()) {
            return null;
        }

        try {
            return ConfigPropertiesUtils.getPropertiesFromFile(file);
        } catch (IOException e) {
            throw new MotechConfigurationException(String.format("Error loading datanucleus properties from %s", file.getAbsolutePath()), e);
        }

    }

    private Properties loadPropertiesFromClasspath(String fileName) {
        Properties properties = new Properties();
        ClassPathResource resource = new ClassPathResource(fileName);
        try (InputStream inputStream = resource.getInputStream()) {
            properties.load(inputStream);
            //After loading properties from classpath we copy file to the default config location
            File file = ConfigPropertiesUtils.getDefaultPropertiesFile(ConfigLocation.FileAccessType.WRITABLE,
                    configLocationFileStore.getAll(), fileName);
            ConfigPropertiesUtils.saveConfig(file, properties);
        } catch (IOException e) {
            LOGGER.warn("Error occurred when loading datanucleus properties from classpath", e);
        }
        return properties;
    }

    private Properties readDatanucleusPropertiesFromFile(File file) {
        Properties datanucleusProperties = null;
        try {
            datanucleusProperties = ConfigPropertiesUtils.getPropertiesFromFile(file);
        } catch (IOException e) {
            throw new MotechConfigurationException(String.format("Error loading datanucleus properties from configuration directory specified by '%s' environment variable",
                    Environment.MOTECH_CONFIG_DIR), e);
        }
        return datanucleusProperties;
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
