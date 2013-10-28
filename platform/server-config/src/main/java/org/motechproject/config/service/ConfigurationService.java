package org.motechproject.config.service;

import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.server.config.domain.MotechSettings;
import org.springframework.cache.annotation.CacheEvict;

import java.util.Properties;

/**
 * <p>Central configuration service that monitors and manages configurations.</p>
 */
public interface ConfigurationService {
    String SETTINGS_CACHE_NAME = "MotechSettings";

    /**
     * <p>Loads bootstrap config that is used to start up the Motech server.</p>
     * <p>
     *     The bootstrap configuration is loaded in the following order:
     *     <ol>
     *         <li>
     *             Load the configuration from <code>bootstrap.properties</code> from the config
     *             directory specified by the environment variable <code>MOTECH_CONFIG_DIR</code>.
     *             <code>bootstrap.properties</code> contains the following properties:
     *             <pre>
     *                 db.url (Mandatory)
     *                 db.username (If required)
     *                 db.password (If required)
     *                 tenant.id (Optional. Defaults to 'DEFAULT')
     *                 config.source (Optional. Defaults to 'UI')
     *             </pre>
     *             An example <code>bootstrap.properties</code> is given below:
     *             <pre>
     *                 db.url=http://localhost:5984
     *                 db.username=motech
     *                 db.password=motech
     *                 tenant.id=MotherChildCare
     *                 config.source=FILE
     *             </pre>
     *         </li>
     *         <li>
     *             If <code>MOTECH_CONFIG_DIR</code> environment variable is <b>not</b> set, load the specific
     *             configuration values from the following environment variables:
     *             <pre>
     *                  MOTECH_DB_URL (Mandatory)
     *                  MOTECH_DB_USERNAME (If required)
     *                  MOTECH_DB_PASSWORD (If required)
     *                  MOTECH_TENANT_ID (Optional. Defaults to 'DEFAULT')
     *                  MOTECH_CONFIG_SOURCE (Optional. Defaults to 'UI')
     *             </pre>
     *         </li>
     *         <li>
     *             If <code>MOTECH_DB_URL</code> environment is not set, load the configuration from
     *             <code>bootstrap.properties</code> from the default MOTECH config directory specified in the file
     *             <code>config-locations.properties</code>.
     *         </li>
     *     </ol>
     * </p>
     * @return Bootstrap configuration
     * @throws org.motechproject.config.MotechConfigurationException if bootstrap configuration cannot be loaded.
     */
    BootstrapConfig loadBootstrapConfig();

    /**
     * <p>
     *     Saves the given <code>BootstrapConfig</code> in the <code>bootstrap.properties</code> file located in
     *     default MOTECH config location. The default motech config location is specified in the file
     *     <code>config-locations.properties</code>.
     * </p>
     *
     * @param bootstrapConfig Bootstrap configuration.
     * @throws org.motechproject.config.MotechConfigurationException if bootstrap configuration cannot be saved.
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
}
