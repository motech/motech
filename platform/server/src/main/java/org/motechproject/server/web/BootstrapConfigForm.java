package org.motechproject.server.web;

/**
 * UI backing Form to capture bootstrap config from UI.
 */

public class BootstrapConfigForm {
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String tenantId;
    private String configSource;

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
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
