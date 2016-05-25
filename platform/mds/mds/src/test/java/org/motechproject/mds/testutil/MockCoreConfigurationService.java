package org.motechproject.mds.testutil;

import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.SQLDBConfig;
import org.motechproject.config.core.exception.MotechConfigurationException;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystemException;
import java.util.Properties;

public class MockCoreConfigurationService implements CoreConfigurationService {

    @Override
    public BootstrapConfig loadBootstrapConfig() {
        Properties bootstrapProperties = loadConfig(ConfigurationConstants.BOOTSTRAP_CONFIG_FILE_NAME);
        return new BootstrapConfig(new SQLDBConfig(bootstrapProperties.getProperty(BootstrapConfig.SQL_URL), bootstrapProperties.getProperty(BootstrapConfig.SQL_DRIVER), bootstrapProperties.getProperty(BootstrapConfig.SQL_USER), bootstrapProperties.getProperty(BootstrapConfig.SQL_PASSWORD)),
                ConfigSource.valueOf(bootstrapProperties.getProperty(BootstrapConfig.CONFIG_SOURCE)),
                bootstrapProperties.getProperty(BootstrapConfig.OSGI_FRAMEWORK_STORAGE),
                bootstrapProperties.getProperty(BootstrapConfig.MOTECH_DIR),
                bootstrapProperties.getProperty(BootstrapConfig.QUEUE_URL),
                getActiveMqConfig());
    }

    @Override
    public Properties loadDatanucleusDataConfig() {
        return loadConfig(ConfigurationConstants.DATANUCLEUS_DATA_SETTINGS_FILE_NAME);
    }

    @Override
    public Properties loadDatanucleusSchemaConfig() {
        return loadConfig(ConfigurationConstants.DATANUCLEUS_SCHEMA_SETTINGS_FILE_NAME);
    }

    @Override
    public Properties loadDatanucleusQuartzConfig() {
        return loadConfig(ConfigurationConstants.DATANUCLEUS_QUARTZ_SETTINGS_FILE_NAME);
    }

    @Override
    public Properties loadFlywayDataConfig() {
        return loadConfig(ConfigurationConstants.FLYWAY_DATA_SETTINGS_FILE_NAME);
    }

    @Override
    public Properties loadFlywaySchemaConfig() {
        return loadConfig(ConfigurationConstants.FLYWAY_SCHEMA_SETTINGS_FILE_NAME);
    }

    public Properties loadConfig(String fileName) {
        Properties properties = new Properties();
        ClassPathResource resource = new ClassPathResource(fileName);
        try {
            try (InputStream is = resource.getInputStream()) {
                properties.load(is);
            }
        } catch (IOException e) {
            throw new MotechConfigurationException("Error when loading properties: " + fileName);
        }

        return properties;
    }

    @Override
    public void saveBootstrapConfig(BootstrapConfig bootstrapConfig) {
    }

    @Override
    public void evictMotechCoreSettingsCache() {
    }

    @Override
    public ConfigLocation getConfigLocation() {
        return null;
    }

    @Override
    public void addConfigLocation(String location) throws FileSystemException {
    }

    @Override
    public Properties getActiveMqConfig() {
        return loadConfig(ConfigurationConstants.BOOTSTRAP_CONFIG_FILE_NAME);
    }
}
