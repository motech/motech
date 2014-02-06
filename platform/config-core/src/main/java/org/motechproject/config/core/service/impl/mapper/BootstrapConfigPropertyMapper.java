package org.motechproject.config.core.service.impl.mapper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.DBConfig;
import org.motechproject.config.core.domain.SQLDBConfig;

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
        Properties properties = new Properties();
        properties.setProperty(BootstrapConfig.COUCHDB_URL, bootstrapConfig.getCouchDbConfig().getUrl());
        setIfNotBlank(properties, BootstrapConfig.COUCHDB_USERNAME, bootstrapConfig.getCouchDbConfig().getUsername());
        setIfNotBlank(properties, BootstrapConfig.COUCHDB_PASSWORD, bootstrapConfig.getCouchDbConfig().getPassword());
        properties.setProperty(BootstrapConfig.SQL_URL, bootstrapConfig.getSqlConfig().getUrl());
        setIfNotBlank(properties, BootstrapConfig.SQL_USER, bootstrapConfig.getSqlConfig().getUsername());
        setIfNotBlank(properties, BootstrapConfig.SQL_PASSWORD, bootstrapConfig.getSqlConfig().getPassword());
        properties.setProperty(BootstrapConfig.TENANT_ID, bootstrapConfig.getTenantId());
        properties.setProperty(BootstrapConfig.CONFIG_SOURCE, bootstrapConfig.getConfigSource().getName());
        return properties;
    }

    private static void setIfNotBlank(Properties properties, String property, String value) {
        if (properties == null || StringUtils.isBlank(property) || StringUtils.isBlank(value)) {
            return;
        }
        properties.setProperty(property, value);
    }

    /**
     * Map from properties to BootstrapConfig object
     *
     * @param bootstrapProperties
     * @return BootstrapConfig object mapped from provided properties.
     */
    public static BootstrapConfig fromProperties(Properties bootstrapProperties) {
        return new BootstrapConfig(new DBConfig(bootstrapProperties.getProperty(BootstrapConfig.COUCHDB_URL),
                bootstrapProperties.getProperty(BootstrapConfig.COUCHDB_USERNAME),
                bootstrapProperties.getProperty(BootstrapConfig.COUCHDB_PASSWORD)),
                new SQLDBConfig(bootstrapProperties.getProperty(BootstrapConfig.SQL_URL),
                bootstrapProperties.getProperty(BootstrapConfig.SQL_USER),
                bootstrapProperties.getProperty(BootstrapConfig.SQL_PASSWORD)),
                bootstrapProperties.getProperty(BootstrapConfig.TENANT_ID),
                ConfigSource.valueOf(bootstrapProperties.getProperty(BootstrapConfig.CONFIG_SOURCE)));
    }
}
