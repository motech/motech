package org.motechproject.server.bootstrap;

/**
 * UI backing Form to capture bootstrap config from UI.
 */

public class BootstrapConfigForm {
    private String sqlUrl;
    private String sqlDriver;
    private String sqlUsername;
    private String sqlPassword;
    private String configSource;
    private String osgiFrameworkStorage;
    private Boolean isCustomFelixPath;
    private String motechDir;
    private String queueUrl;

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

    public String getConfigSource() {
        return configSource;
    }

    public void setConfigSource(String configSource) {
        this.configSource = configSource;
    }

    public void setOsgiFrameworkStorage(String felixPath) {
        this.osgiFrameworkStorage = felixPath;
    }

    public String getOsgiFrameworkStorage() {
        return osgiFrameworkStorage;
    }

    public void setIsCustomFelixPath(Boolean customFelixPath) {
        this.isCustomFelixPath = customFelixPath;
    }

    public Boolean getIsCustomFelixPath() {
        return isCustomFelixPath;
    }

    public String getMotechDir() {
        return this.motechDir;
    }

    public void setMotechDir(String motechDir) {
        this.motechDir = motechDir;
    }

    public String getQueueUrl() {
        return queueUrl;
    }

    public void setQueueUrl(String queueUrl) {
        this.queueUrl = queueUrl;
    }
}
