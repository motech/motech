package org.motechproject.config.core.service.impl.mapper;

import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.DBConfig;

import java.util.Properties;

/**
 * This class provides static utility methods for Mapping between <code>BootstrapConfig</code>
 * and <code>Properties</code>
 */
public final class BootstrapConfigPropertyMapper {

    private BootstrapConfigPropertyMapper() {
    }

    /**
     * Map from BootstrapConfig to Properties
     *
     * @param bootstrapConfig
     * @return Properties mapped from provided bootstrapConfig.
     */
    public static Properties toProperties(BootstrapConfig bootstrapConfig) {
        Properties bootstrapProperties = new Properties();
        bootstrapProperties.setProperty(BootstrapConfig.DB_URL, bootstrapConfig.getDbConfig().getUrl());
        bootstrapProperties.setProperty(BootstrapConfig.DB_USERNAME, bootstrapConfig.getDbConfig().getUsername());
        bootstrapProperties.setProperty(BootstrapConfig.DB_PASSWORD, bootstrapConfig.getDbConfig().getPassword());
        bootstrapProperties.setProperty(BootstrapConfig.TENANT_ID, bootstrapConfig.getTenantId());
        bootstrapProperties.setProperty(BootstrapConfig.CONFIG_SOURCE, bootstrapConfig.getConfigSource().getName());
        return bootstrapProperties;
    }

    /**
     * Map from properties to BootstrapConfig object
     *
     * @param bootstrapProperties
     * @return BootstrapConfig object mapped from provided properties.
     */
    public static BootstrapConfig fromProperties(Properties bootstrapProperties) {
        return new BootstrapConfig(new DBConfig(bootstrapProperties.getProperty(BootstrapConfig.DB_URL),
                bootstrapProperties.getProperty(BootstrapConfig.DB_USERNAME),
                bootstrapProperties.getProperty(BootstrapConfig.DB_PASSWORD)),
                bootstrapProperties.getProperty(BootstrapConfig.TENANT_ID),
                ConfigSource.valueOf(bootstrapProperties.getProperty(BootstrapConfig.CONFIG_SOURCE)));
    }
}
