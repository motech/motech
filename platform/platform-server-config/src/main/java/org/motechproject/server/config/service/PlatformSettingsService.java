package org.motechproject.server.config.service;

import org.ektorp.CouchDbConnector;
import org.motechproject.server.config.settings.MotechSettings;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public interface PlatformSettingsService {
    String SETTINGS_DB = "motech-platform-startup";
    String SETTINGS_CACHE_NAME = "MotechSettings";
    String SETTINGS_FILE_NAME = "motech-settings.conf";

    MotechSettings getPlatformSettings();

    void savePlatformSettings(Properties settings);

    void setPlatformSetting(final String key, final String value);

    String getPlatformLanguage();

    String getPlatformLanguage(final String defaultValue);

    Locale getPlatformLocale();

    Properties exportPlatformSettings();

    /**
     * Add new location for config file.
     *
     * @param location file location
     * @param save     true if you always want to load the config file from given location, otherwise false
     */
    void addConfigLocation(final String location, final boolean save);

    void saveBundleProperties(final String bundleSymbolicName, final String fileName, final Properties properties) throws IOException;

    void saveRawConfig(final String bundleSymbolicName, final String filename, final InputStream rawData) throws IOException;

    InputStream getRawConfig(String bundleSymbolicName, String filename) throws IOException;

    boolean rawConfigExists(String bundleSymbolicName, String filename);

    List<String> listRawConfigNames(String bundleSymbolicName);

    List<String> retrieveRegisteredBundleNames();

    Properties getBundleProperties(final String bundleSymbolicName, final String fileName) throws IOException;

    Map<String, Properties> getAllProperties(final String bundleSymbolicName) throws IOException;

    CouchDbConnector getCouchConnector(String dbName);

    void evictMotechSettingsCache();
}
