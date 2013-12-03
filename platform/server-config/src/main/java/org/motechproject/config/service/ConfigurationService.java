package org.motechproject.config.service;

import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.server.config.domain.SettingsRecord;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * <p>Central configuration service that monitors and manages configurations.</p>
 */
public interface ConfigurationService {
    String SETTINGS_CACHE_NAME = "MotechSettings";

    /**
     * <p>Loads bootstrap config that is used to start up the Motech server.</p>
     * <p>
     * The bootstrap configuration is loaded in the following order:
     * <ol>
     * <li>
     * Load the configuration from <code>bootstrap.properties</code> from the config
     * directory specified by the environment variable <code>MOTECH_CONFIG_DIR</code>.
     * <code>bootstrap.properties</code> contains the following properties:
     * <pre>
     *                 db.url (Mandatory)
     *                 db.username (If required)
     *                 db.password (If required)
     *                 tenant.id (Optional. Defaults to 'DEFAULT')
     *                 config.source (Optional. Defaults to 'UI')
     *             </pre>
     * An example <code>bootstrap.properties</code> is given below:
     * <pre>
     *                 db.url=http://localhost:5984
     *                 db.username=motech
     *                 db.password=motech
     *                 tenant.id=MotherChildCare
     *                 config.source=FILE
     *             </pre>
     * </li>
     * <li>
     * If <code>MOTECH_CONFIG_DIR</code> environment variable is <b>not</b> set, load the specific
     * configuration values from the following environment variables:
     * <pre>
     *                  MOTECH_DB_URL (Mandatory)
     *                  MOTECH_DB_USERNAME (If required)
     *                  MOTECH_DB_PASSWORD (If required)
     *                  MOTECH_TENANT_ID (Optional. Defaults to 'DEFAULT')
     *                  MOTECH_CONFIG_SOURCE (Optional. Defaults to 'UI')
     *             </pre>
     * </li>
     * <li>
     * If <code>MOTECH_DB_URL</code> environment is not set, load the configuration from
     * <code>bootstrap.properties</code> from the default MOTECH config directory specified in the file
     * <code>config-locations.properties</code>.
     * </li>
     * </ol>
     * </p>
     *
     * @return Bootstrap configuration
     * @throws org.motechproject.config.core.MotechConfigurationException
     *          if bootstrap configuration cannot be loaded.
     */
    BootstrapConfig loadBootstrapConfig();

    /**
     * <p>
     * Saves the given <code>BootstrapConfig</code> in the <code>bootstrap.properties</code> file located in
     * default MOTECH config location. The default motech config location is specified in the file
     * <code>config-locations.properties</code>.
     * </p>
     *
     * @param bootstrapConfig Bootstrap configuration.
     * @throws org.motechproject.config.core.MotechConfigurationException
     *          if bootstrap configuration cannot be saved.
     */
    void save(BootstrapConfig bootstrapConfig);

    MotechSettings getPlatformSettings();

    @CacheEvict(value = SETTINGS_CACHE_NAME, allEntries = true)
    void savePlatformSettings(Properties settings);

    @CacheEvict(value = SETTINGS_CACHE_NAME, allEntries = true)
    void setPlatformSetting(String key, String value);

    @CacheEvict(value = SETTINGS_CACHE_NAME, allEntries = true)
    void evictMotechSettingsCache();

    @CacheEvict(value = SETTINGS_CACHE_NAME, allEntries = true)
    void savePlatformSettings(MotechSettings settings);

    /**
     * <p>
     * Uses current configuration and default one to find changed properties and then connects them with annotations.
     * Moreover creates file with non default configurations and packs is into the zip file.
     * </p>
     *
     * @param propertyFile name of exported file
     * @return FileInputStream that contains zip file
     * @throws IOException
     */
    FileInputStream createZipWithConfigFiles(String propertyFile, String fileName) throws IOException;

    /**
     * <p>
     * This method allows to check whether MOTECH is currently running in the FILE or UI mode
     * </p>
     *
     * @return Current Config Source
     */
    ConfigSource getConfigSource();

    /**
     * <p>
     * Retrieves merged properties, given default set. Depending on the ConfigSource, it will either
     * merge default properties with the properties from DB or get properties from file.
     * </p>
     *
     * @param module            The module we wish to retrieve properties for
     * @param filename          Resource filename
     * @param defaultProperties Default properties of the module
     * @return Merged properties of the certain module
     * @throws IOException if module properties cannot be read from file
     */
    Properties getModuleProperties(String module, String filename, Properties defaultProperties) throws IOException;

    /**
     * <p>
     * Depending on the config source, it will either store properties in the DB or file.
     * Only properties that are different from the default ones are stored. If the properties
     * database record or file doesn't exist yet for the given module, it will be created.
     * </p>
     *
     * @param module            The module we wish to update properties for
     * @param filename          Resource filename
     * @param newProperties     New properties to store
     * @param defaultProperties Default properties of the module
     * @throws IOException if module properties cannot be retrieved from file
     */
    void addOrUpdateProperties(String module, String filename, Properties newProperties, Properties defaultProperties) throws IOException;

    /**
     * Adds, updates, or deletes configurations in FILE mode only.
     * Files are classified as either raw config or properties based on the extension of the file.
     * Uses CouchDb's bulk operations.
     *
     * @param files Files to read configuration from.
     */
    void processExistingConfigs(List<File> files);

    /**
     * Saves both property and raw configurations in FILE mode only.
     * Files are classified as either raw config or properties based on the extension of the file.
     *
     * @param file File to read configuration from.
     */
    void addOrUpdate(File file);

    /**
     * <p>
     * Retrieves all the module properties and returns them as Map, where key is the
     * filename.
     * </p>
     *
     * @param module            The module we wish to retrieve properties for
     * @param defaultProperties Default properties of the module
     * @return Properties mapped by filename
     * @throws IOException if any of the module properties file cannot be read
     */
    Map<String, Properties> getAllModuleProperties(String module, Map<String, Properties> defaultProperties) throws IOException;

    /**
     * <p>
     * Allows persisting of raw json properties either in the database or file, depending on the selected
     * ConfigSource mode.
     * </p>
     *
     * @param module   Module we wish to save properties for
     * @param filename Resource filename
     * @param rawData  Raw JSON data to persist
     * @throws IOException
     */
    void saveRawConfig(final String module, final String filename, final InputStream rawData) throws IOException;

    /**
     * <p>
     * Allows to retrieve raw JSON data either from the database or file, depending on the specified
     * ConfigSource mode.
     * </p>
     *
     * @param module   Module we wish to retrieve raw data for
     * @param filename Resource filename
     * @param resource Resource file containing default rawConfig, in case no other has been found
     * @return Raw JSON data as InputStream
     * @throws IOException
     */
    InputStream getRawConfig(String module, String filename, Resource resource) throws IOException;

    /**
     * <p>
     * Allows to check if raw data has been registered for specified module
     * </p>
     *
     * @param module   Module symbolic name
     * @param filename Resource filename
     * @return True if raw data exists for given parameters, false otherwise
     */
    boolean rawConfigExists(String module, String filename);

    /**
     * <p>
     * Depending on the selected ConfigSource mode, this method looks for registered bundle properties
     * and returns a list of files it has found
     * </p>
     *
     * @return List of files with registered properties
     */
    List<String> retrieveRegisteredBundleNames();

    /**
     * <p>
     * Depending on the selected ConfigSource mode, this method looks for all registered raw data
     * properties within the specified module.
     * </p>
     *
     * @param module Module we wish to perform look for
     * @return List of filenames that register raw config for specified module
     */
    List<String> listRawConfigNames(String module);

    /**
     * <p>
     * Checks if given module registers certain property file
     * </p>
     *
     * @param module   Module we wish to perform check for
     * @param filename Resource filename
     * @return True if properties exist, false otherwise
     */
    boolean registersProperties(String module, String filename);

    /**
     * Adds a new config location and restarts the monitor.
     *
     * @param newConfigLocation New config location
     */
    void updateConfigLocation(String newConfigLocation);

    /**
     * Deletes the db record corresponding to the file.
     *
     * @param file File that has been deleted.
     */
    void delete(File file);

    SettingsRecord loadDefaultConfig();

    SettingsRecord loadConfig();
}
