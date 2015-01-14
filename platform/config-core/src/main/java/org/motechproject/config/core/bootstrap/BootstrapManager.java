package org.motechproject.config.core.bootstrap;

import org.motechproject.config.core.domain.BootstrapConfig;

import java.util.Properties;

/**
 * Classes implementing this interface are responsible for loading and saving the bootstrap configuration.
 * This configuration contains all the information necessary for starting a MOTECH server, such as database
 * configuration, configuration source, tenant ID and ActiveMQ configuration.
 */
public interface BootstrapManager {

    String BOOTSTRAP_PROPERTIES = "bootstrap.properties";

    /**
     * Loads bootstrap configuration from several resources. It will try to load the configuration from file under
     * specified location, if it fails an attempt to load the configuration from environmental variables will be made.
     * If it also fails manager will try to load the configuration from file under default location.
     *
     * @return the bootstrap configuration
     */
    BootstrapConfig loadBootstrapConfig();

    /**
     * Saves given configuration to file.
     *
     * @param bootstrapConfig  the configuration to be saved
     */
    void saveBootstrapConfig(BootstrapConfig bootstrapConfig);

    /**
     * Returns ActiveMQ configuration, which is a part of {@code BootstrapConfig}.
     *
     * @return the configuration of the ActiveMQ
     */
    Properties getActiveMqConfig();
}
