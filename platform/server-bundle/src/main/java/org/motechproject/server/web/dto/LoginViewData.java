package org.motechproject.server.web.dto;

import org.motechproject.server.config.domain.LoginMode;
import org.motechproject.server.web.form.LoginForm;

import java.util.Locale;

/**
 * Class that holds data for login view
 */
public class LoginViewData {

    private String openIdProviderName;
    private String openIdProviderUrl;
    private LoginForm loginForm;
    private String error;
    private LoginMode loginMode;
    private Locale pageLang;
    private String contextPath;

    public String getOpenIdProviderName() {
        return openIdProviderName;
    }

    public void setOpenIdProviderName(String openIdProviderName) {
        this.openIdProviderName = openIdProviderName;
    }

    public String getOpenIdProviderUrl() {
        return openIdProviderUrl;
    }

    public void setOpenIdProviderUrl(String openIdProviderUrl) {
        this.openIdProviderUrl = openIdProviderUrl;
    }

    public LoginForm getLoginForm() {
        return loginForm;
    }

    public void setLoginForm(LoginForm loginForm) {
        this.loginForm = loginForm;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public LoginMode getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(LoginMode loginMode) {
        this.loginMode = loginMode;
    }

    public Locale getPageLang() {
        return pageLang;
    }

    public void setPageLang(Locale pageLang) {
        this.pageLang = pageLang;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
