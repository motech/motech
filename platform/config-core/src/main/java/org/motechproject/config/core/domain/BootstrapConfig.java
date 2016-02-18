package org.motechproject.config.core.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.config.core.exception.MotechConfigurationException;
import org.motechproject.config.core.validator.QueueURLValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <p>Represents the bootstrap configuration object. It is composed of:
 * <ol>
 * <li>DBConfig - represents the database related bootstrap object.</li>
 * <li>Configuration source - represents the source of configuration (FILE / UI).</li>
 * <li>ActiveMq Config - represents the properties of ActiveMq.</li>
 * </ol>
 * </p>
 */
public class BootstrapConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(BootstrapConfig.class);

    public static final String SQL_URL = "sql.url";
    public static final String SQL_USER = "sql.user";
    public static final String SQL_PASSWORD = "sql.password";
    public static final String CONFIG_SOURCE = "config.source";
    public static final String SQL_DRIVER = "sql.driver";
    public static final String OSGI_FRAMEWORK_STORAGE = "org.osgi.framework.storage";
    public static final String QUEUE_URL = "jms.broker.url";

    public static final String DEFAULT_OSGI_FRAMEWORK_STORAGE = new File(System.getProperty("user.home"), ".motech"+File.separator+"felix-cache").getAbsolutePath();
    private SQLDBConfig sqlConfig;
    private String osgiFrameworkStorage;
    private String queueUrl;
    private Properties activeMqProperties;

    private ConfigSource configSource;

    /**
     * Constructor.
     *
     * @param sqlConfig  the configuration of a SQL database
     * @param configSource  the source from which MOTECH configuration should be read
     * @param osgiFrameworkStorage  the directory used as the bundle cache
     * @param queueUrl  the URL of the JMS broker
     */
    public BootstrapConfig(SQLDBConfig sqlConfig, ConfigSource configSource, String osgiFrameworkStorage, String queueUrl) {
        this(sqlConfig, configSource, osgiFrameworkStorage, queueUrl, null);
    }

    /**
     * Constructor.
     *
     * @param sqlConfig  the configuration of a SQL database
     * @param configSource  the source from which MOTECH configuration should be read
     * @param osgiFrameworkStorage  the directory used as the bundle cache
     * @param queueUrl  the URL of the JMS broker
     * @param activeMqProperties  the ActiveMQ properties
     * @throws MotechConfigurationException if sqlConfig is null.
     */
    public BootstrapConfig(SQLDBConfig sqlConfig, ConfigSource configSource, String osgiFrameworkStorage, String queueUrl, Properties activeMqProperties) {
        if (sqlConfig == null) {
            throw new MotechConfigurationException("DB configuration cannot be null.");
        }
        this.sqlConfig = sqlConfig;
        this.configSource = (configSource != null) ? configSource : ConfigSource.UI;
        this.osgiFrameworkStorage = (StringUtils.isNotBlank(osgiFrameworkStorage)) ? osgiFrameworkStorage : DEFAULT_OSGI_FRAMEWORK_STORAGE;
        this.activeMqProperties = setActiveMqProperties(activeMqProperties, queueUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BootstrapConfig that = (BootstrapConfig) o;

        if (!configSource.equals(that.configSource)) {
            return false;
        }

        if (!sqlConfig.equals(that.sqlConfig)) {
            return false;
        }

        if (!queueUrl.equals(that.queueUrl)) {
            return false;
        }

        if (!activeMqProperties.equals(that.activeMqProperties)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = sqlConfig.hashCode();
        result = 31 * result + configSource.hashCode();
        result = 31 * result + osgiFrameworkStorage.hashCode();
        result = 31 * result + queueUrl.hashCode();
        result = 31 * result + activeMqProperties.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BootstrapConfig{");
        sb.append(", sqlConfig=").append(sqlConfig);
        sb.append(", configSource=").append(configSource);
        sb.append(", osgiFrameworkStorage=").append(osgiFrameworkStorage);
        sb.append(", queueUrl=").append(queueUrl);
        sb.append(", activeMqProperties=").append(activeMqProperties);
        sb.append('}');
        return sb.toString();
    }

    public ConfigSource getConfigSource() {
        return configSource;
    }

    public SQLDBConfig getSqlConfig() {
        return sqlConfig;
    }

    public String getOsgiFrameworkStorage() {
        return osgiFrameworkStorage;
    }

    public String getQueueUrl() {
        return queueUrl;
    }

    public Properties getActiveMqProperties() {
        return activeMqProperties;
    }

    public final void setQueueUrl(String queueUrl) {
        this.queueUrl = queueUrl;
        QueueURLValidator queueURLValidator = new QueueURLValidator();
        queueURLValidator.validate(queueUrl);
    }

    private Properties setActiveMqProperties(Properties propertiesFromFile, String queueUrl) {
        Properties properties = new Properties();

        try (InputStream in = getClass().getClassLoader().getResourceAsStream("activemq-default.properties")) {
            properties.load(in);
        } catch (IOException e) {
            LOGGER.error("IOException while loading default activeMQ properties from file", e);
        }

        if (queueUrl == null) {
            setQueueUrl(properties.getProperty(QUEUE_URL));
        } else {
            setQueueUrl(queueUrl);
            properties.setProperty(QUEUE_URL, queueUrl);
        }

        if (propertiesFromFile != null) {
            properties.putAll(propertiesFromFile);
        }

        return properties;
    }
}
