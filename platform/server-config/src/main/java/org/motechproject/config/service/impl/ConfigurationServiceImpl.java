package org.motechproject.config.service.impl;

import org.apache.log4j.Logger;
import org.motechproject.config.bootstrap.BootstrapConfigManager;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link org.motechproject.config.service.ConfigurationService}.
 */
@Service("configurationService")
public class ConfigurationServiceImpl implements ConfigurationService {
    private BootstrapConfigManager bootstrapConfigManager;
    private static Logger logger = Logger.getLogger(ConfigurationServiceImpl.class);

    @Autowired
    public ConfigurationServiceImpl(BootstrapConfigManager bootstrapConfigManager) {
        this.bootstrapConfigManager = bootstrapConfigManager;
    }

    @Override
    public BootstrapConfig loadBootstrapConfig() {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading bootstrap configuration.");
        }

        final BootstrapConfig bootstrapConfig = bootstrapConfigManager.loadBootstrapConfig();

        if (logger.isDebugEnabled()) {
            logger.debug("BootstrapConfig:" + bootstrapConfig);
        }

        return bootstrapConfig;
    }

    @Override
    public void save(BootstrapConfig bootstrapConfig) {
        if (logger.isDebugEnabled()) {
            logger.debug("Saving bootstrap configuration.");
        }

        bootstrapConfigManager.saveBootstrapConfig(bootstrapConfig);

        if (logger.isDebugEnabled()) {
            logger.debug("Saved bootstrap configuration:" + bootstrapConfig);
        }
    }
}
