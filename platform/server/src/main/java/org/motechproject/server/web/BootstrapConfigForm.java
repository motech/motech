package org.motechproject.server.web;

/**
 * UI backing Form to capture bootstrap config from UI.
 */

public class BootstrapConfigForm {
    private String couchDbUrl;
    private String couchDbUsername;
    private String couchDbPassword;
    private String sqlUrl;
    private String sqlUsername;
    private String sqlPassword;
    private String tenantId;
    private String configSource;

    public String getCouchDbUrl() {
        return couchDbUrl;
    }

    public void setCouchDbUrl(String couchDbUrl) {
        this.couchDbUrl = couchDbUrl;
    }

    public String getCouchDbUsername() {
        return couchDbUsername;
    }

    public void setCouchDbUsername(String couchDbUsername) {
        this.couchDbUsername = couchDbUsername;
    }

    public String getCouchDbPassword() {
        return couchDbPassword;
    }

    public void setCouchDbPassword(String couchDbPassword) {
        this.couchDbPassword = couchDbPassword;
    }

    public String getSqlUrl() {
        return sqlUrl;
    }

    public void setSqlUrl(String sqlUrl) {
        this.sqlUrl = sqlUrl;
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
