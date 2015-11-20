package org.motechproject.config.core.service.impl.mapper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.SQLDBConfig;

import java.util.Properties;

/**
 * This class provides static utility methods for Mapping between <code>BootstrapConfig</code>
 * and <code>Properties</code>
 */
public final class BootstrapConfigPropertyMapper {

    /**
     * This is an utility class and should not be initiated.
     */
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
        properties.setProperty(BootstrapConfig.SQL_URL, bootstrapConfig.getSqlConfig().getUrl());
        properties.setProperty(BootstrapConfig.SQL_DRIVER, bootstrapConfig.getSqlConfig().getDriver());
        setIfNotBlank(properties, BootstrapConfig.SQL_USER, bootstrapConfig.getSqlConfig().getUsername());
        setIfNotBlank(properties, BootstrapConfig.SQL_PASSWORD, bootstrapConfig.getSqlConfig().getPassword());
        properties.setProperty(BootstrapConfig.CONFIG_SOURCE, bootstrapConfig.getConfigSource().getName());
        setIfNotBlank(properties, BootstrapConfig.OSGI_FRAMEWORK_STORAGE, bootstrapConfig.getOsgiFrameworkStorage());
        properties.putAll(bootstrapConfig.getActiveMqProperties());

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

        return new BootstrapConfig(
            new SQLDBConfig(bootstrapProperties.getProperty(BootstrapConfig.SQL_URL),
                    bootstrapProperties.getProperty(BootstrapConfig.SQL_DRIVER),
                    bootstrapProperties.getProperty(BootstrapConfig.SQL_USER),
                    bootstrapProperties.getProperty(BootstrapConfig.SQL_PASSWORD)),
                    ConfigSource.valueOf(bootstrapProperties.getProperty(BootstrapConfig.CONFIG_SOURCE)),
                    bootstrapProperties.getProperty(BootstrapConfig.OSGI_FRAMEWORK_STORAGE),
                    bootstrapProperties.getProperty(BootstrapConfig.QUEUE_URL),
                    getActiveMqProperties(bootstrapProperties));
    }

    private static Properties getActiveMqProperties(Properties properties) {
        Properties activeMqProperties = new Properties();

        if (properties != null) {
            for (Object key : properties.keySet()) {
                String keyString = (String) key;
                if (keyString.startsWith("jms.") || keyString.startsWith("motech.message")) {
                    activeMqProperties.put(key, properties.get(key));
                }
            }
        }

        return activeMqProperties.isEmpty() ? null : activeMqProperties;
    }
}
