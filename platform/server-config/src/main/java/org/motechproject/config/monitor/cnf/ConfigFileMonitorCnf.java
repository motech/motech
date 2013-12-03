package org.motechproject.config.monitor.cnf;

import org.apache.commons.vfs.FileSystemException;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.config.monitor.ConfigFileMonitor;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.service.ConfigLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Config class to create ConfigFileMonitor bean.
 */
@Configuration
public class ConfigFileMonitorCnf {

    @Bean
    @Autowired
    public ConfigFileMonitor configFileMonitor(ConfigLoader configLoader, ConfigurationService configurationService,
                                               CoreConfigurationService coreConfigurationService) throws FileSystemException {

        final ConfigSource configSource = coreConfigurationService.loadBootstrapConfig().getConfigSource();
        if (ConfigSource.FILE.equals(configSource)) {
            return new ConfigFileMonitor(configLoader, configurationService, coreConfigurationService);
        }
        return null;
    }
}
