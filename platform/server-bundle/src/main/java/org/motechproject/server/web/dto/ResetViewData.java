package org.motechproject.server.web.dto;

import org.motechproject.server.web.form.ResetForm;

import java.lang.String;
import java.util.List;
import java.util.Locale;

/*
 * Class responsible for holding data shared between reset controller and its view
 */
public class ResetViewData {
    private ResetForm resetForm;
    private boolean isResetSucceed;
    private boolean isInvalidToken;
    private List<String> errors;
    private Locale pageLang;

    public ResetForm getResetForm() {
        return resetForm;
    }

    public void setResetForm(ResetForm resetForm) {
        this.resetForm = resetForm;
    }

    public boolean isResetSucceed() {
        return isResetSucceed;
    }

    public void setResetSucceed(boolean isResetSucceed) {
        this.isResetSucceed = isResetSucceed;
    }

    public boolean isInvalidToken() {
        return isInvalidToken;
    }

    public void setInvalidToken(boolean invalidToken) {
        this.isInvalidToken = invalidToken;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Locale getPageLang() {
        return pageLang;
    }

    public void setPageLang(Locale pageLang) {
        this.pageLang = pageLang;
    }
}
