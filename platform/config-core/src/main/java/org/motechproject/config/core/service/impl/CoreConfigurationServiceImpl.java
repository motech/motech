package org.motechproject.config.core.service.impl;

import org.apache.log4j.Logger;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.bootstrap.BootstrapManager;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.core.datanucleus.DatanucleusManager;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.filestore.ConfigLocationFileStore;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.Properties;

/**
 * Implementation of {@link org.motechproject.config.core.service.CoreConfigurationService}.
 * <p/>
 * This class is concerned with managing the Bootstrap configuration.
 */
@Component("coreConfigurationService")
public class CoreConfigurationServiceImpl implements CoreConfigurationService {

    private static final Logger LOGGER = Logger.getLogger(CoreConfigurationServiceImpl.class);

    private ConfigLocationFileStore configLocationFileStore;
    private BootstrapManager bootstrapManager;
    private DatanucleusManager datanucleusManager;

    @Autowired
    public CoreConfigurationServiceImpl(BootstrapManager bootstrapManager, DatanucleusManager datanucleusManager, ConfigLocationFileStore configLocationFileStore) {
        this.configLocationFileStore = configLocationFileStore;
        this.bootstrapManager = bootstrapManager;
        this.datanucleusManager = datanucleusManager;
    }

    /**
     * This method is used to return the bootstrap configuration
     * <p/>
     * Try to Load and return the bootstrap configuration in the following order:
     * - Environment Variable: MOTECH_CONFIG_DIR - bootstrap props are in MOTECH_CONFIG_DIR/bootstrap.properties
     * - Environment Variables:
     * MOTECH_SQL_URL, MOTECH_SQL_USERNAME, MOTECH_SQL_PASSWORD, MOTECH_SQL_DRIVER - Database config is loaded from these
     * environment variables.
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
    @Caching(cacheable = {@Cacheable(value = CORE_SETTINGS_CACHE_NAME, key = "#root.methodName") })
    public BootstrapConfig loadBootstrapConfig() {
        return bootstrapManager.loadBootstrapConfig();
    }

    @Override
    @Caching(cacheable = {@Cacheable(value = CORE_SETTINGS_CACHE_NAME, key = "#root.methodName") })
    public Properties loadDatanucleusConfig() {
        return datanucleusManager.getDatanucleusProperties();
    }

    /**
     * Saves the bootstrap configuration provided, to the default Bootstrap file location.
     *
     * @param bootstrapConfig
     */
    @Override
    public void saveBootstrapConfig(BootstrapConfig bootstrapConfig) {
        bootstrapManager.saveBootstrapConfig(bootstrapConfig);
    }

    @Override
    public ConfigLocation getConfigLocation() {
        Iterable<ConfigLocation> configLocations = configLocationFileStore.getAll();
        StringBuilder sb = new StringBuilder("");
        for (ConfigLocation configLocation : configLocations) {
            sb.append(configLocation.getLocation()).append(' ');
            Resource configLocationResource = configLocation.toResource();
            try {
                Resource motechSettings = configLocationResource.createRelative(ConfigurationConstants.SETTINGS_FILE_NAME);
                if (motechSettings.isReadable()) {
                    return configLocation;
                }
                LOGGER.warn("Could not read motech-settings.properties from: " + configLocationResource.toString());
            } catch (IOException e) {
                LOGGER.warn("Problem reading motech-settings.properties from location: " + configLocationResource.toString(), e);
            }
        }
        throw new MotechConfigurationException(String.format("Could not read settings from any of the config locations. Searched directories: %s.", sb));
    }

    @Override
    public void addConfigLocation(String location) throws FileSystemException {
        configLocationFileStore.add(location);
        LOGGER.info("Changed config file location");
    }

    @Override
    public Properties getActiveMqConfig() {
        return bootstrapManager.getActiveMqConfig();
    }

    @Override
    public void evictMotechCoreSettingsCache() {
        // Left blank.
        // Annotation will automatically remove all cached motech settings
    }

}
