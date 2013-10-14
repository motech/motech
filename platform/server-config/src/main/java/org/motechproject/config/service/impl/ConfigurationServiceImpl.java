package org.motechproject.config.service.impl;

import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;
import org.motechproject.config.bootstrap.BootstrapConfigManager;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.monitor.ConfigFileMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link org.motechproject.config.service.ConfigurationService}.
 */
@Service("configurationService")
public class ConfigurationServiceImpl implements ConfigurationService {

    private static Logger logger = Logger.getLogger(ConfigurationServiceImpl.class);

    private ConfigFileMonitor configFileMonitor;
    private BootstrapConfigManager bootstrapConfigManager;

    @Autowired
    public ConfigurationServiceImpl(BootstrapConfigManager bootstrapConfigManager, ConfigFileMonitor configFileMonitor) {
        this.bootstrapConfigManager = bootstrapConfigManager;
        this.configFileMonitor = configFileMonitor;
    }

    @Override
    public BootstrapConfig loadBootstrapConfig() {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading bootstrap configuration.");
        }

        final BootstrapConfig bootstrapConfig = bootstrapConfigManager.loadBootstrapConfig();

        if (bootstrapConfig == null) {
            return null;
        }

        if (ConfigSource.FILE.equals(bootstrapConfig.getConfigSource())) {
            try {
                configFileMonitor.monitor();
            } catch (FileSystemException e) {
                logger.error("Can't start config file monitor. ", e);
            }
        }

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
