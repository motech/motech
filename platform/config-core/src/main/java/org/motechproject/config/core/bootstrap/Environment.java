package org.motechproject.config.core.bootstrap;

/**
 * <p>Represents the system environment.</p>
 *
 * This defines the required System Environmental Variables.
 */
public interface Environment {

    String MOTECH_CONFIG_DIR = "MOTECH_CONFIG_DIR";
    String MOTECH_SQL_URL = "MOTECH_SQL_URL";
    String MOTECH_SQL_USERNAME = "MOTECH_SQL_USERNAME";
    String MOTECH_SQL_PASSWORD = "MOTECH_SQL_PASSWORD";
    String MOTECH_TENANT_ID = "MOTECH_TENANT_ID";
    String MOTECH_CONFIG_SOURCE = "MOTECH_CONFIG_SOURCE";
    String MOTECH_SQL_DRIVER = "MOTECH_SQL_DRIVER";

    String getConfigDir();

    String getSqlUrl();

    String getSqlUsername();

    String getSqlPassword();

    String getTenantId();

    String getConfigSource();

    String getSqlDriver();
}
