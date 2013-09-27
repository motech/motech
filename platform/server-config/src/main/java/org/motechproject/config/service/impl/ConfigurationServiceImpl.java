package org.motechproject.config.service.impl;

import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;
import org.motechproject.config.bootstrap.BootstrapConfigLoader;
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

    @Autowired
    private BootstrapConfigLoader bootstrapConfigLoader;

    @Autowired
    private ConfigFileMonitor configFileMonitor;

    @Override
    public BootstrapConfig loadBootstrapConfig() {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading bootstrap configuration.");
        }

        final BootstrapConfig bootstrapConfig = bootstrapConfigLoader.loadBootstrapConfig();
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
}
