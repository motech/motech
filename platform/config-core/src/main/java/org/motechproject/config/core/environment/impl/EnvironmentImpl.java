package org.motechproject.config.core.environment.impl;

import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.environment.Environment;
import org.motechproject.config.core.filestore.ConfigPropertiesUtils;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Implementation of the {@link org.motechproject.config.core.environment.Environment}
 * Component to loading environment variables
 */
@Component
public class EnvironmentImpl implements Environment {

    @Override
    public String getConfigDir() {
        return getValue(MOTECH_CONFIG_DIR);
    }

    @Override
    public Properties getProperties(String varName) {
        return ConfigPropertiesUtils.getPropertiesFromSystemVarString(getValue(varName));
    }

    @Override
    public Properties getDatanucleusDataProperties() {
        return getProperties(MOTECH_DATANUCLEUS_DATA_ROPERTIES);
    }

    @Override
    public Properties getDatanucleusSchemaProperties() {
        return getProperties(MOTECH_DATANUCLEUS_SCHEMA_PROPERTIES);
    }

    @Override
    public Properties getBootstrapProperties() {
        Properties bootstrapProperties = new Properties();
        bootstrapProperties.put(BootstrapConfig.SQL_URL, getValue(MOTECH_SQL_URL));
        bootstrapProperties.put(BootstrapConfig.SQL_USER, getValue(MOTECH_SQL_USERNAME));
        bootstrapProperties.put(BootstrapConfig.SQL_PASSWORD, getValue(MOTECH_SQL_PASSWORD));
        bootstrapProperties.put(BootstrapConfig.CONFIG_SOURCE, getValue(MOTECH_CONFIG_SOURCE));
        bootstrapProperties.put(BootstrapConfig.SQL_DRIVER, getValue(MOTECH_SQL_DRIVER));
        bootstrapProperties.put(BootstrapConfig.OSGI_FRAMEWORK_STORAGE, getValue(MOTECH_OSGI_FRAMEWORK_STORAGE));
        bootstrapProperties.put(BootstrapConfig.MOTECH_DIR, getValue(MOTECH_DIRECTORY));
        bootstrapProperties.put(BootstrapConfig.QUEUE_URL, getValue(MOTECH_QUEUE_URL));
        return bootstrapProperties;
    }

    @Override
    public Properties getActiveMqProperties() {
        return ConfigPropertiesUtils.getPropertiesFromSystemVarString(getValue(MOTECH_ACTIVEMQ_PROPERTIES));
    }

    @Override
    public String getValue(String variableName) {
        String value = System.getenv(variableName);
        return value == null ? "" : value;
    }
}
