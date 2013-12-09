package org.motechproject.config.core.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.bootstrap.Environment;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.DBConfig;
import org.motechproject.config.core.filestore.ConfigFileReader;
import org.motechproject.config.core.filestore.ConfigLocationFileStore;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.config.core.service.impl.mapper.BootstrapConfigPropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileSystemException;
import java.util.Properties;

import static org.motechproject.config.core.domain.ConfigLocation.FileAccessType;

/**
 * Implementation of {@link org.motechproject.config.core.service.CoreConfigurationService}.
 * <p/>
 * This class is concerned with managing the Bootstrap configuration.
 */
@Component("coreConfigurationService")
public class CoreConfigurationServiceImpl implements CoreConfigurationService {

    private static Logger logger = Logger.getLogger(CoreConfigurationServiceImpl.class);

    static final String BOOTSTRAP_PROPERTIES = "bootstrap.properties";

    private Environment environment;
    private ConfigFileReader configFileReader;
    private ConfigLocationFileStore configLocationFileStore;

    @Autowired
    public CoreConfigurationServiceImpl(ConfigFileReader configFileReader, Environment environment, ConfigLocationFileStore configLocationFileStore) {
        this.environment = environment;
        this.configFileReader = configFileReader;
        this.configLocationFileStore = configLocationFileStore;
    }

    /**
     * This method is used to return the bootstrap configuration
     * <p/>
     * Try to Load and return the bootstrap configuration in the following order:
     * - Environment Variable: MOTECH_CONFIG_DIR - bootstrap props are in MOTECH_CONFIG_DIR/bootstrap.properties
     * - Environment Variables:
     * MOTECH_DB_URL, MOTECH_DB_USERNAME, MOTECH_DB_PASSWORD - Database config is loaded from these environment variables.
     * MOTECH_TENANT_ID - Tenant ID to be used. If not specified, “DEFAULT” will be used as the tenant id.
     * MOTECH_CONFIG_SOURCE - Configuration source to be used.
     * - Default config location - bootstrap props in bootstrap.properties file from the default location.
     * Default location of bootstrap.properties file is specified in config-locations.properties.
     * <p/>
     * Returns the Bootstrapconfig if bootstrap config is defined in any of the above locations.
     *
     * @return BootstrapConfig object
     * @throws MotechConfigurationException if there is no bootstrap.properties file found.
     */
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
            logger.info("Could not find bootstrap configuration values from environment variables. So, trying to load " +
                    "from default location.", e);
            return readBootstrapConfigFromDefaultLocation();
        }
    }

    /**
     * Saves the bootstrap configuration provided, to the default Bootstrap file location.
     *
     * @param bootstrapConfig
     */
    @Override
    public void saveBootstrapConfig(BootstrapConfig bootstrapConfig) {
        File defaultBootstrapFile = getDefaultBootstrapFile(FileAccessType.WRITABLE);
        try {
            defaultBootstrapFile.getParentFile().mkdirs();
            defaultBootstrapFile.createNewFile();

            Properties bootstrapProperties = BootstrapConfigPropertyMapper.toProperties(bootstrapConfig);

            try (Writer writer = new FileWriter(defaultBootstrapFile)) {
                bootstrapProperties.store(writer, "MOTECH bootstrap properties.");
            }
        } catch (IOException e) {
            String errorMessage = "Error saving bootstrap properties to file";
            logger.error(errorMessage + " " + e.getMessage());
            throw new MotechConfigurationException(errorMessage, e);
        }
    }

    @Override
    public ConfigLocation getConfigLocation() {
        Iterable<ConfigLocation> configLocations = configLocationFileStore.getAll();
        for (ConfigLocation configLocation : configLocations) {
            Resource configLocationResource = configLocation.toResource();
            try {
                Resource motechSettings = configLocationResource.createRelative(ConfigurationConstants.SETTINGS_FILE_NAME);
                if (motechSettings.isReadable()) {
                    return configLocation;
                }
                logger.warn("Could not read motech-settings.properties from: " + configLocationResource.toString());
            } catch (IOException e) {
                logger.warn("Problem reading motech-settings.properties from location: " + configLocationResource.toString(), e);
            }
        }
        throw new MotechConfigurationException("Could not read settings from any of the config locations.");
    }

    @Override
    public void addConfigLocation(String location) throws FileSystemException {
        configLocationFileStore.add(location);
        logger.info("Changed config file location");
    }

    File getDefaultBootstrapFile(FileAccessType accessType) {
        Iterable<ConfigLocation> configLocations = configLocationFileStore.getAll();

        for (ConfigLocation configLocation : configLocations) {
            try {
                return configLocation.getFile(BOOTSTRAP_PROPERTIES, accessType);
            } catch (MotechConfigurationException e) {
                logger.warn(e.getMessage());
            }
        }

        throw new MotechConfigurationException(String.format("%s file is not %s from any of the default locations.", BOOTSTRAP_PROPERTIES, accessType.toString()));
    }

    private BootstrapConfig readBootstrapConfigFromDefaultLocation() {
        File bootstrapFile;
        try {
            bootstrapFile = getDefaultBootstrapFile(FileAccessType.READABLE);
        } catch (MotechConfigurationException ex) {
            logger.warn(ex.getMessage());
            throw ex;
        }

        return readBootstrapConfigFromFile(bootstrapFile, StringUtils.EMPTY);
    }

    private String getConfigFile(String configLocation) {
        return configLocation + File.separator + BOOTSTRAP_PROPERTIES;
    }

    private BootstrapConfig readBootstrapConfigFromEnvironment() {
        String dbUrl = environment.getDBUrl();
        String username = environment.getDBUsername();
        String password = environment.getDBPassword();
        String tenantId = environment.getTenantId();
        String configSource = environment.getConfigSource();

        return new BootstrapConfig(new DBConfig(dbUrl, username, password), tenantId, ConfigSource.valueOf(configSource));
    }

    private BootstrapConfig readBootstrapConfigFromFile(File configFile, String errorMessage) {
        try {
            logger.debug("Trying to load bootstrap configuration from " + configFile.getAbsolutePath());

            Properties properties = configFileReader.getProperties(configFile);
            return BootstrapConfigPropertyMapper.fromProperties(properties);
        } catch (IOException e) {
            final String message = "Error loading bootstrap properties from config file " + configFile + " " + errorMessage;
            logger.warn(message);
            throw new MotechConfigurationException(message, e);
        }
    }
}
