package org.motechproject.config.core.service;

import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigLocation;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.nio.file.FileSystemException;
import java.util.Properties;

/**
 * Loads and saves the core configuration required to start the Motech instance.
 */
public interface CoreConfigurationService {

    String CORE_SETTINGS_CACHE_NAME = "MotechCoreSettings";

    /**
     * Loads the bootstrap configuration.
     *
     * @return bootstrap configuration.
     */
    BootstrapConfig loadBootstrapConfig();

    /**
     * Loads the datanucleus configuration for data database
     *
     * @return datanucleus configuration for data database
     */
    @CacheEvict(value = CORE_SETTINGS_CACHE_NAME, allEntries = true)
    Properties loadDatanucleusDataConfig();

    /**
     * Loads the datanucleus configuration for schema database
     *
     * @return datanucleus configuration for schema database
     */
    @CacheEvict(value = CORE_SETTINGS_CACHE_NAME, allEntries = true)
    Properties loadDatanucleusSchemaConfig();

    /**
     * Loads the datanucleus configuration for schema database
     *
     * @return datanucleus configuration for schema database
     */
    @CacheEvict(value = CORE_SETTINGS_CACHE_NAME, allEntries = true)
    Properties loadDatanucleusQuartzConfig();

    /**
     * Loads the Flyway configuration for the data database.
     * @return Flyway configuration for the data database
     */
    @Caching(cacheable = {@Cacheable(value = CORE_SETTINGS_CACHE_NAME, key = "#root.methodName") })
    Properties loadFlywayDataConfig();

    /**
     * Loads the Flyway configuration for the schema database.
     * @return Flyway configuration for the schema database
     */
    @Caching(cacheable = {@Cacheable(value = CORE_SETTINGS_CACHE_NAME, key = "#root.methodName") })
    Properties loadFlywaySchemaConfig();

    /**
     * Saves the bootstrap configuration
     *
     * @param bootstrapConfig Bootstrap config
     */
    @CacheEvict(value = CORE_SETTINGS_CACHE_NAME, allEntries = true)
    void saveBootstrapConfig(BootstrapConfig bootstrapConfig);

    /**
     * Removes all cached MOTECH settings.
     */
    @CacheEvict(value = CORE_SETTINGS_CACHE_NAME, allEntries = true)
    void evictMotechCoreSettingsCache();

    /**
     * Returns the config location where all the config files are present.
     * @return configLocation.
     */
    ConfigLocation getConfigLocation();

    /**
     * Adds the new config location to the list of existing config locations where configurations are loaded from in the file system.
     *
     * @param location config location to add.
     * @throws FileSystemException
     */
    void addConfigLocation(final String location) throws FileSystemException;

    /**
     * Returns the ActiveMq properties.
     * @return activeMq properties.
     */
    Properties getActiveMqConfig();
}
