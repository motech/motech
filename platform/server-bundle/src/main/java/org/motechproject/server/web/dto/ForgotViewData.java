package org.motechproject.server.web.dto;

import org.motechproject.server.config.domain.LoginMode;

import java.util.Locale;

/**
 * Class that holds data for forgot view
 */
public class ForgotViewData {

    private boolean isEmailGetter;
    private boolean isProcessed;

    private String email;
    private String error;
    private LoginMode loginMode;
    private Locale pageLang;
    private String contextPath;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailGetter() {
        return isEmailGetter;
    }

    public void setEmailGetter(boolean isEmailGetter) {
        this.isEmailGetter = isEmailGetter;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean isProcessed) {
        this.isProcessed = isProcessed;
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
