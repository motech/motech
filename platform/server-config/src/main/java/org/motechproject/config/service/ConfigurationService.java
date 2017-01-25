package org.motechproject.config.service;

import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.exception.MotechConfigurationException;
import org.motechproject.config.domain.ModulePropertiesRecord;
import org.motechproject.config.domain.MotechSettings;
import org.motechproject.config.domain.SettingsRecord;
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
     *                 sql.url (Mandatory)
     *                 sql.driver (Mandatory)
     *                 sql.username (If required)
     *                 sql.password (If required)
     *                 config.source (Optional. Defaults to 'UI')
     *             </pre>
     * An example <code>bootstrap.properties</code> is given below:
     * <pre>
     *                 sql.url=jdbc:mysql://localhost:3306/
     *                 sql.driver=com.mysql.jdbc.Driver
     *                 sql.username=motech
     *                 sql.password=motech
     *                 config.source=FILE
     *             </pre>
     * </li>
     * <li>
     * If <code>MOTECH_CONFIG_DIR</code> environment variable is <b>not</b> set, load the specific
     * configuration values from the following environment variables:
     * <pre>
     *                  MOTECH_SQL_URL (Mandatory)
     *                  MOTECH_SQL_DRIVER (Mandatory)
     *                  MOTECH_SQL_USERNAME (If required)
     *                  MOTECH_SQL_PASSWORD (If required)
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
     * @throws MotechConfigurationException if bootstrap configuration cannot be loaded.
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
     * @throws MotechConfigurationException if bootstrap configuration cannot be saved.
     */
    void save(BootstrapConfig bootstrapConfig);

    MotechSettings getPlatformSettings();

    /**
     * Saves given platform settings to the settings service. Available platform settings are language, login mode,
     * provider name, provider URL, server URL, status message timeout, and upload size.
     *
     * @param settings  the settings to be saved
     */
    @CacheEvict(value = SETTINGS_CACHE_NAME, allEntries = true)
    void savePlatformSettings(Properties settings);

    /**
     * Sets given value for the platform setting with given key.
     *
     * @param key  the setting name
     * @param value  the value to be set
     */
    @CacheEvict(value = SETTINGS_CACHE_NAME, allEntries = true)
    void setPlatformSetting(String key, String value);

    /**
     * Removes all cached MOTECH settings.
     */
    @CacheEvict(value = SETTINGS_CACHE_NAME, allEntries = true)
    void evictMotechSettingsCache();

    /**
     * Saves given MOTECH settings to the settings service.
     *
     * @param settings  the settings to be saved
     */
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
     * @param bundle            The bundle we wish to retrieve properties for
     * @param filename          Resource filename
     * @param defaultProperties Default properties of the bundle
     * @return Merged properties of the certain bundle
     * @throws IOException if bundle properties cannot be read from file
     */
    Properties getBundleProperties(String bundle, String filename, Properties defaultProperties) throws IOException;

    /**
     * <p>
     * Depending on the config source, it will either store properties in the DB or file.
     * Only properties that are different from the default ones are stored. If the properties
     * database record or file doesn't exist yet for the given bundle, it will be created.
     * </p>
     *
     * @param bundle            Symbolic name of updated bundle
     * @param version           Version of updated bundle
     * @param filename          Resource filename
     * @param newProperties     New properties to store
     * @param defaultProperties Default properties of the bundle
     * @throws IOException if bundle properties cannot be retrieved from file
     */
    void addOrUpdateProperties(String bundle, String version, String filename, Properties newProperties, Properties defaultProperties) throws IOException;

    /**
     * <p>
     *     Works similar to <code>addOrUpdateProperties</code> but instead of just adding / updating properties
     *     checks database for any deprecated properties and removes to ensure that only current ones are available
     * </p>
     *
     * @param bundle            Symbolic name of updated bundle
     * @param version           Version of updated bundle
     * @param filename          Resource filename
     * @param newProperties     New properties to store
     * @param defaultProperties Default properties of the bundle
     * @throws IOException if bundle properties cannot be retrieved from file
     */
    void updatePropertiesAfterReinstallation(String bundle, String version, String filename, Properties defaultProperties, Properties newProperties) throws IOException;

    /**
     * <p>
     *     Removes properties for given bundle.
     * </p>
     * @param bundle The bundle we wish to remove properties for
     */
    void removeAllBundleProperties(String bundle);

    /**
     * Adds, updates, or deletes configurations in FILE mode only.
     * Files are classified as either raw config or properties based on the extension of the file.s
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
     * Retrieves all the bundle properties and returns them as Map, where key is the
     * filename.
     * </p>
     *
     * @param bundle            The bundle we wish to retrieve properties for
     * @param defaultProperties Default properties of the bundle
     * @return Properties mapped by filename
     * @throws IOException if any of the bundle properties file cannot be read
     */
    Map<String, Properties> getAllBundleProperties(String bundle, Map<String, Properties> defaultProperties) throws IOException;

    /**
     * <p>
     * Allows persisting of raw json properties either in the database or file, depending on the selected
     * ConfigSource mode.
     * </p>
     *
     * @param bundle   Bundle we wish to save properties for
     * @param filename Resource filename
     * @param rawData  Raw JSON data to persist
     * @throws IOException
     */
    void saveRawConfig(final String bundle, final String version, final String filename, final InputStream rawData) throws IOException;

    /**
     * <p>
     * Allows to retrieve raw JSON data either from the database or file, depending on the specified
     * ConfigSource mode.
     * </p>
     *
     * @param bundle   Bundle we wish to retrieve raw data for
     * @param filename Resource filename
     * @param resource Resource file containing default rawConfig, in case no other has been found
     * @return Raw JSON data as InputStream
     * @throws IOException
     */
    InputStream getRawConfig(String bundle, String filename, Resource resource) throws IOException;

    /**
     * <p>
     * Allows to check if raw data has been registered for specified bundle
     * </p>
     *
     * @param bundle   Bundle symbolic name
     * @param filename Resource filename
     * @return True if raw data exists for given parameters, false otherwise
     */
    boolean rawConfigExists(String bundle, String filename);

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
     * properties within the specified bundle.
     * </p>
     *
     * @param bundle Bundle we wish to perform look for
     * @return List of filenames that register raw config for specified bundle
     */
    List<String> listRawConfigNames(String bundle);

    /**
     * <p>
     * Checks if given bundle registers certain property file
     * </p>
     *
     * @param bundle   Bundle we wish to perform check for
     * @param filename Resource filename
     * @return True if properties exist, false otherwise
     */
    boolean registersProperties(String bundle, String filename);

    /**
     * Adds a new config location and restarts the monitor.
     *
     * @param newConfigLocation New config location
     */
    void updateConfigLocation(String newConfigLocation);

    /**
     * Deletes the db records corresponding to the bundle with given bundle symbolic name.
     */
    void deleteByBundle(String bundle);

    /**
     * Deletes the db record corresponding to the bundle and filename.
     */
    void deleteByBundleAndFileName(String bundle, String filename);

    /**
     * Loads the default config for MOTECH from the resource file.
     *
     * @return default settings
     */
    SettingsRecord loadDefaultConfig();

    /**
     * Loads current MOTECH configuration
     *
     * @return current MOTECH settings
     */
    SettingsRecord loadConfig();

    /**
     * Checks whether set MOTECH configuration requires the configuraton files to be present
     *
     * @return true if files are required, false otherwise
     */
    boolean requiresConfigurationFiles();

    /**
     * Bulk add or update method for the Bundle Properties records. Iterates through
     * the passed records and either adds them, if they are not present, or updates otherwise.
     *
     * @param records a list of properties records
     */
    void addOrUpdateBundleRecords(List<ModulePropertiesRecord> records);

    /**
     * Removes given bundle properties records
     *
     * @param records a list of properties records to remove
     */
    void removeBundleRecords(List<ModulePropertiesRecord> records);

    /**
     * A convenient method for adding or updating the properties, which determines on its
     * own whether the record should be added or updated
     *
     * @param record a record to store
     */
    void addOrUpdateBundleRecord(ModulePropertiesRecord record);

    /**
     * Adds or updates a MOTECH settings to the settings service.
     * @param settingsRecord a settings record to store
     */
    void addOrUpdateSettings(SettingsRecord settingsRecord);
}
