package org.motechproject.config.service.impl;

import org.apache.log4j.Logger;
import org.motechproject.config.bootstrap.BootstrapConfigLoader;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link org.motechproject.config.service.ConfigurationService}.
 */
@Component
public class ConfigurationServiceImpl implements ConfigurationService {
    private BootstrapConfigLoader bootstrapConfigLoader;
    private static Logger logger = Logger.getLogger(ConfigurationServiceImpl.class);

    @Autowired
    public ConfigurationServiceImpl(BootstrapConfigLoader bootstrapConfigLoader) {
        this.bootstrapConfigLoader = bootstrapConfigLoader;
    }

    @Override
    public BootstrapConfig loadBootstrapConfig() {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading bootstrap configuration.");
        }

        final BootstrapConfig bootstrapConfig = bootstrapConfigLoader.loadBootstrapConfig();

        if (logger.isDebugEnabled()) {
            logger.debug("BootstrapConfig:" + bootstrapConfig);
        }
        return bootstrapConfig;
    }
}
