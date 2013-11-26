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
     * @param bootstrapConfig
     */
    void saveBootstrapConfig(BootstrapConfig bootstrapConfig);

    /**
     * Gets the list of configuration locations in the order of priority.
     * @return config locations.
     */
    Iterable<ConfigLocation> getConfigLocations();

    /**
     * Adds the new config location to the list of existing config locations where configurations are loaded from in the file system.
     *
     * @param location config location to add.
     * @throws FileSystemException
     */
    void addConfigLocation(final String location) throws FileSystemException;
}
