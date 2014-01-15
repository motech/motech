package org.motechproject.config.core.bootstrap;

import org.motechproject.config.core.domain.BootstrapConfig;

/**
 * Classes inheriting this interface are responsible for loading and saving the bootstrap configuration.
 */
public interface BootstrapManager {

    String BOOTSTRAP_PROPERTIES = "bootstrap.properties";

    BootstrapConfig loadBootstrapConfig();

    void saveBootstrapConfig(BootstrapConfig bootstrapConfig);
}
