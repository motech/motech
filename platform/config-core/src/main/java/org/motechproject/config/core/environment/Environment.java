package org.motechproject.config.core.environment;

import java.util.Properties;

/**
 * Classes implementing this interface are responsible for loading environment variables
 */
public interface Environment {

    String MOTECH_DATANUCLEUS_DATA_ROPERTIES = "MOTECH_DATANUCLEUS_DATA_PROPERTIES";
    String MOTECH_DATANUCLEUS_SCHEMA_PROPERTIES = "MOTECH_DATANUCLEUS_SCHEMA_PROPERTIES";
    String MOTECH_SQL_URL = "MOTECH_SQL_URL";
    String MOTECH_SQL_USERNAME = "MOTECH_SQL_USERNAME";
    String MOTECH_SQL_PASSWORD = "MOTECH_SQL_PASSWORD";
    String MOTECH_SQL_DRIVER = "MOTECH_SQL_DRIVER";
    String MOTECH_CONFIG_DIR = "MOTECH_CONFIG_DIR";
    String MOTECH_CONFIG_SOURCE = "MOTECH_CONFIG_SOURCE";
    String MOTECH_OSGI_FRAMEWORK_STORAGE = "MOTECH_OSGI_FRAMEWORK_STORAGE";
    String MOTECH_QUEUE_URL = "MOTECH_QUEUE_URL";
    String MOTECH_ACTIVEMQ_PROPERTIES = "MOTECH_ACTIVEMQ_PROPERTIES";

    /**
     * Returns the path to the Motech configuration directory specified by the MOTECH_CONFIG_DIR environment variable.
     * Used by {@link org.motechproject.config.core.bootstrap.BootstrapManager} and {@link org.motechproject.config.core.datanucleus.DatanucleusManager}
     * to find bootstrap.properties, datanucleus_schema.properties and datanucleus_data.properties files.
     *
     * @return the configuration directory path
     */
    String getConfigDir();

    /**
     * Returns datanucleus properties from MOTECH_DATANUCLEUS_DATA_ROPERTIES environment variable.
     *
     * @return the datanucleus properties for data database
     */
    Properties getDatanucleusDataProperties();

    /**
     * Returns datanucleus properties from MOTECH_DATANUCLEUS_SCHEMA_PROPERTIES environment variable.
     *
     * @return the datanucleus properties for schema database
     */
    Properties getDatanucleusSchemaProperties();

    /**
     * Returns bootstrap properties from environment variables:
     * MOTECH_SQL_URL, MOTECH_SQL_USERNAME, MOTECH_SQL_PASSWORD, MOTECH_SQL_DRIVER,
     * MOTECH_CONFIG_SOURCE, MOTECH_OSGI_FRAMEWORK_STORAGE, MOTECH_QUEUE_URL.
     *
     * @return the bootstrap properties
     */
    Properties getBootstrapPropperties();

    /**
     * Returns ActiveMq properties from MOTECH_ACTIVEMQ_PROPERTIES environment variable.
     *
     * @return the ActiveMq properties
     */
    Properties getActiveMqProperties();

    /**
     * Returns environment variable value.
     *
     * @param variableName the name of environment variable
     * @return the variable value if it exist, otherwise blank String
     */
    String getValue(String variableName);

}
