package org.motechproject.config.core.service;

import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigLocation;

import java.nio.file.FileSystemException;

/**
 * Loads and saves the core configuration required to start the Motech instance.
 */
public interface CoreConfigurationService {

    /**
     * Loads the bootstrap configuration.
     *
     * @return bootstrap configuration.
     */
    BootstrapConfig loadBootstrapConfig();

    /**
     * Saves the bootstrap configuration
     *
     * @param bootstrapConfig Bootstrap config
     */
    void saveBootstrapConfig(BootstrapConfig bootstrapConfig);

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
}
