package org.motechproject.server.web;

/**
 * UI backing Form to capture bootstrap config from UI.
 */

public class BootstrapConfigForm {
    private String sqlUrl;
    private String sqlDriver;
    private String sqlUsername;
    private String sqlPassword;
    private String tenantId;
    private String configSource;

    public String getSqlUrl() {
        return sqlUrl;
    }

    public void setSqlUrl(String sqlUrl) {
        this.sqlUrl = sqlUrl;
    }

    public String getSqlDriver() {
        return sqlDriver;
    }

    public void setSqlDriver(String sqlDriver) {
        this.sqlDriver = sqlDriver;
    }

    public String getSqlUsername() {
        return sqlUsername;
    }

    public void setSqlUsername(String sqlUsername) {
        this.sqlUsername = sqlUsername;
    }

    public String getSqlPassword() {
        return sqlPassword;
    }

    public void setSqlPassword(String sqlPassword) {
        this.sqlPassword = sqlPassword;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getConfigSource() {
        return configSource;
    }

    public void setConfigSource(String configSource) {
        this.configSource = configSource;
    }
}
