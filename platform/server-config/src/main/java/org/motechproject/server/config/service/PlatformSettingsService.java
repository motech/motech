package org.motechproject.server.config.service;

import java.io.IOException;
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

    void evictBundleSettingsCache();

}
