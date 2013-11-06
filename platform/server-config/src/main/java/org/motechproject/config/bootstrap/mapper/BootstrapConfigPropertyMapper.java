package org.motechproject.config.bootstrap.mapper;

import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.domain.DBConfig;

import java.util.Properties;

import static org.apache.commons.lang.StringUtils.defaultString;
import static org.motechproject.config.domain.BootstrapConfig.CONFIG_SOURCE;
import static org.motechproject.config.domain.BootstrapConfig.DB_PASSWORD;
import static org.motechproject.config.domain.BootstrapConfig.DB_URL;
import static org.motechproject.config.domain.BootstrapConfig.DB_USERNAME;
import static org.motechproject.config.domain.BootstrapConfig.TENANT_ID;

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
        bootstrapProperties.setProperty(DB_URL, defaultString(bootstrapConfig.getDbConfig().getUrl()));
        bootstrapProperties.setProperty(DB_USERNAME, defaultString(bootstrapConfig.getDbConfig().getUsername()));
        bootstrapProperties.setProperty(DB_PASSWORD, defaultString(bootstrapConfig.getDbConfig().getPassword()));
        bootstrapProperties.setProperty(TENANT_ID, defaultString(bootstrapConfig.getTenantId()));
        bootstrapProperties.setProperty(CONFIG_SOURCE, defaultString(bootstrapConfig.getConfigSource().getName()));
        return bootstrapProperties;
    }

    /**
     * Map from properties to BootstrapConfig object
     *
     * @param bootstrapProperties
     * @return BootstrapConfig object mapped from provided properties.
     */
    public static BootstrapConfig fromProperties(Properties bootstrapProperties) {
        return new BootstrapConfig(new DBConfig(bootstrapProperties.getProperty(DB_URL),
                bootstrapProperties.getProperty(DB_USERNAME),
                bootstrapProperties.getProperty(DB_PASSWORD)),
                bootstrapProperties.getProperty(TENANT_ID),
                ConfigSource.valueOf(bootstrapProperties.getProperty(CONFIG_SOURCE)));
    }
}
