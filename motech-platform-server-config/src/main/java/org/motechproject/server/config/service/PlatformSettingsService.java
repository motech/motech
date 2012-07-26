package org.motechproject.server.config.service;

import org.ektorp.CouchDbConnector;
import org.motechproject.server.config.settings.MotechSettings;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public interface PlatformSettingsService {
    public static final String SETTINGS_DB = "motech-platform-startup";

    MotechSettings getPlatformSettings();

    void savePlatformSettings(Properties settings);

    void setPlatformSetting(final String key, final String value);

    String getPlatformLanguage();

    String getPlatformLanguage(final String defaultValue);

    Locale getPlatformLocale();

    Properties exportPlatformSettings();

    /**
     * Add location from which {@link org.motechproject.server.config.ConfigLoader} will attempt to load config file
     *
     * @param location file location
     * @param save     true if you want to {@link org.motechproject.server.config.ConfigLoader} always load the config file from given location, otherwise false
     * @throws IOException
     */
    void addConfigLocation(final String location, final boolean save) throws IOException;

    void saveBundleProperties(final String bundleSymbolicName, final String fileName, final Properties properties) throws IOException;

    List<String> retrieveRegisteredBundleNames();

    Properties getBundleProperties(final String bundleSymbolicName, final String fileName) throws IOException;

    Map<String, Properties> getAllProperties(final String bundleSymbolicName) throws IOException;

    CouchDbConnector getCouchConnector(String dbName);
}
