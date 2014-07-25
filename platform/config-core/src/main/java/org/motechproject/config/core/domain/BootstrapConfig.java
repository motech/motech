package org.motechproject.config.core.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.config.core.MotechConfigurationException;

/**
 * <p>Represents the bootstrap configuration object. It is composed of:
 * <ol>
 * <li>DBConfig - represents the database related bootstrap object.</li>
 * <li>Tenant ID - represents the identifier of the tenant.</li>
 * <li>Configuration source - represents the source of configuration (FILE / UI).</li>
 * </ol>
 * </p>
 */
public class BootstrapConfig {
    public static final String SQL_URL = "sql.url";
    public static final String SQL_USER = "sql.user";
    public static final String SQL_PASSWORD = "sql.password";
    public static final String TENANT_ID = "tenant.id";
    public static final String CONFIG_SOURCE = "config.source";
    public static final String SQL_DRIVER = "sql.driver";

    public static final String DEFAULT_TENANT_ID = "DEFAULT";
    private String tenantId;
    private SQLDBConfig sqlConfig;

    private ConfigSource configSource;

    /**
     * @param sqlConfig
     * @param tenantId
     * @param configSource
     * @throws org.motechproject.config.core.MotechConfigurationException if dbConfig is null.
     */
    public BootstrapConfig(SQLDBConfig sqlConfig, String tenantId, ConfigSource configSource) {
        if (sqlConfig == null) {
            throw new MotechConfigurationException("DB configuration cannot be null.");
        }
        this.sqlConfig = sqlConfig;
        this.tenantId = (StringUtils.isNotBlank(tenantId)) ? tenantId : DEFAULT_TENANT_ID;
        this.configSource = (configSource != null) ? configSource : ConfigSource.UI;
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

        if (!tenantId.equals(that.tenantId)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = sqlConfig.hashCode();
        result = 31 * result + tenantId.hashCode();
        result = 31 * result + configSource.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BootstrapConfig{");
        sb.append(", sqlConfig=").append(sqlConfig);
        sb.append(", tenantId='").append(tenantId).append('\'');
        sb.append(", configSource=").append(configSource);
        sb.append('}');
        return sb.toString();
    }

    public String getTenantId() {
        return tenantId;
    }

    public ConfigSource getConfigSource() {
        return configSource;
    }

    public SQLDBConfig getSqlConfig() {
        return sqlConfig;
    }

}
