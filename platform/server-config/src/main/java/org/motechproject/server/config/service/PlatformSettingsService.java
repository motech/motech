package org.motechproject.server.config.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Platform Settings service used to handle platform settings.
 */
public interface PlatformSettingsService {
    String BUNDLE_CACHE_NAME = "BundleSettings";

    Properties exportPlatformSettings();

    /**
     * Add new location for config file.
     *
     * @param location file location
     */
    void addConfigLocation(final String location) throws IOException;

    void saveBundleProperties(final String bundleSymbolicName, final String fileName, final Properties properties) throws IOException;

    void saveRawConfig(final String bundleSymbolicName, final String filename, final InputStream rawData) throws IOException;

    InputStream getRawConfig(String bundleSymbolicName, String filename) throws IOException;

    boolean rawConfigExists(String bundleSymbolicName, String filename);

    List<String> listRawConfigNames(String bundleSymbolicName);

    List<String> retrieveRegisteredBundleNames();

    Properties getBundleProperties(final String bundleSymbolicName, final String fileName) throws IOException;

    Map<String, Properties> getAllProperties(final String bundleSymbolicName) throws IOException;

    void evictBundleSettingsCache();

}
