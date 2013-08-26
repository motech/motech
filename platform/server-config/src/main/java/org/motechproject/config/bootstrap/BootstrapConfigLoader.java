package org.motechproject.config.bootstrap;

import org.motechproject.config.domain.BootstrapConfig;

/**
 * Loads the bootstrap configuration required to start the Motech instance.
 *
 * @see org.motechproject.config.service.ConfigurationService
 */
public interface BootstrapConfigLoader {

    /**
     * Loads the bootstrap configuration.
     *
     * @return bootstrap configuration.
     */
    BootstrapConfig loadBootstrapConfig();
}
