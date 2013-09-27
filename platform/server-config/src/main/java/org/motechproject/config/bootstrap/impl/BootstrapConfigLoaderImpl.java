package org.motechproject.config.bootstrap.impl;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.log4j.Logger;
import org.motechproject.config.MotechConfigurationException;
import org.motechproject.config.bootstrap.BootstrapConfigLoader;
import org.motechproject.config.bootstrap.ConfigFileReader;
import org.motechproject.config.bootstrap.Environment;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.domain.DBConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Default implementation of {@link BootstrapConfigLoader}.
 */
@Component
public class BootstrapConfigLoaderImpl implements BootstrapConfigLoader {

    private static Logger logger = Logger.getLogger(BootstrapConfigLoaderImpl.class);

    private static final String BOOTSTRAP_PROPERTIES = "bootstrap.properties";
    private static final String DB_URL = "db.url";
    private static final String DB_USERNAME = "db.username";
    private static final String DB_PASSWORD = "db.password";
    private static final String TENANT_ID = "tenant.id";
    private static final String CONFIG_SOURCE = "config.source";
    private static final String DEFAULT_BOOTSTRAP_CONFIG_DIR_PROP = "default.bootstrap.config.dir";
    static final String DEFAULT_BOOTSTRAP_CONFIG_DIR = "/etc/motech/config";

    private Environment environment;
    private ConfigFileReader configFileReader;
    private String defaultBootstrapConfigDir;


    @Autowired
    public BootstrapConfigLoaderImpl(ConfigFileReader configFileReader, Environment environment, Properties configProperties) {
        this.environment = environment;
        this.configFileReader = configFileReader;
        final String defaultConfigDir = StrSubstitutor.replace(configProperties.getProperty(DEFAULT_BOOTSTRAP_CONFIG_DIR_PROP), System.getProperties());
        this.defaultBootstrapConfigDir = defaultConfigDir != null ? defaultConfigDir : DEFAULT_BOOTSTRAP_CONFIG_DIR;
    }

    String getDefaultBootstrapConfigDir() {
        return defaultBootstrapConfigDir;
    }

    @Override
    public BootstrapConfig loadBootstrapConfig() {
        String configLocation = environment.getConfigDir();

        if (configLocation != null) {
            final String errorMessage = String.format("specified by '%s' environment variable.", Environment.MOTECH_CONFIG_DIR);
            return readBootstrapConfigFromFile(getConfigFile(configLocation), errorMessage);
        }

        try {
            return readBootstrapConfigFromEnvironment();
        } catch (MotechConfigurationException e) {
            logger.warn("Could not find bootstrap configuration values from environment variables. So, trying to load " +
                    "from default config file " + defaultBootstrapConfigDir, e);
            return readBootstrapConfigFromFile(getConfigFile(defaultBootstrapConfigDir), "");
        }
    }

    private String getConfigFile(String configLocation) {
        return configLocation + "/" + BOOTSTRAP_PROPERTIES;
    }

    private BootstrapConfig readBootstrapConfigFromEnvironment() {
        String dbUrl = environment.getDBUrl();
        String username = environment.getDBUsername();
        String password = environment.getDBPassword();
        String tenantId = environment.getTenantId();
        String configSource = environment.getConfigSource();

        return new BootstrapConfig(new DBConfig(dbUrl, username, password), tenantId, ConfigSource.valueOf(configSource));
    }

    private BootstrapConfig readBootstrapConfigFromFile(String configFile, String errorMessage) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to load bootstrap configuration from " + configFile);
            }

            Properties properties = configFileReader.getProperties(new File(configFile));
            String dbUrl = properties.getProperty(DB_URL);
            String username = properties.getProperty(DB_USERNAME);
            String password = properties.getProperty(DB_PASSWORD);
            String tenantId = properties.getProperty(TENANT_ID);
            String configSource = properties.getProperty(CONFIG_SOURCE);

            return new BootstrapConfig(new DBConfig(dbUrl, username, password), tenantId, ConfigSource.valueOf(configSource));

        } catch (IOException e) {
            final String message = "Error loading bootstrap properties from config file " + configFile + " " + errorMessage;
            logger.error(message, e);
            throw new MotechConfigurationException(message, e);
        }
    }
}
