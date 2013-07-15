package org.motechproject.server.web.form;

import static org.apache.commons.lang.StringUtils.EMPTY;

public class StartupForm {
    private String language = EMPTY;
    private String queueUrl = EMPTY;
    private String schedulerUrl = EMPTY;
    private String adminLogin = EMPTY;
    private String adminPassword = EMPTY;
    private String adminConfirmPassword = EMPTY;
    private String adminEmail = EMPTY;
    private String loginMode = EMPTY;
    private String providerName = EMPTY;
    private String providerUrl = EMPTY;

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderUrl() {
        return providerUrl;
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getQueueUrl() {
        return queueUrl;
    }

    public void setQueueUrl(final String queueUrl) {
        this.queueUrl = queueUrl;
    }

    public String getSchedulerUrl() {
        return schedulerUrl;
    }

    public void setSchedulerUrl(final String schedulerUrl) {
        this.schedulerUrl = schedulerUrl;
    }

    public String getAdminLogin() {
        return adminLogin;
    }

    public void setAdminLogin(String adminLogin) {
        this.adminLogin = adminLogin;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getAdminConfirmPassword() {
        return adminConfirmPassword;
    }

    public void setAdminConfirmPassword(String adminConfirmPassword) {
        this.adminConfirmPassword = adminConfirmPassword;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(String loginMode) {
        this.loginMode = loginMode;
    }
}
