package org.motechproject.config.bootstrap;

import org.motechproject.config.domain.BootstrapConfig;

/**
 * Loads and saves the bootstrap configuration required to start the Motech instance.
 *
 * @see org.motechproject.config.service.ConfigurationService
 */
public interface BootstrapConfigManager {

    /**
     * Loads the bootstrap configuration.
     *
     * @return bootstrap configuration.
     */
    BootstrapConfig loadBootstrapConfig();

    /**
     * Saves the bootstrap configuration
     *
     * @param bootstrapConfig
     */
    void saveBootstrapConfig(BootstrapConfig bootstrapConfig);
}
