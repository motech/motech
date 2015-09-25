package org.motechproject.server.web.dto;

import org.motechproject.server.web.form.ChangePasswordForm;

import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for holding data shared between reset controller and the change password view
 */
public class ChangePasswordViewData {

    private ChangePasswordForm changePasswordForm;

    private List<String> errors;

    private boolean changingSucceed;

    public ChangePasswordViewData(ChangePasswordForm changePasswordForm) {
        this.changePasswordForm = changePasswordForm;
        this.errors = new ArrayList<>();
        this.changingSucceed = false;
    }

    public ChangePasswordForm getChangePasswordForm() {
        return changePasswordForm;
    }

    public void setChangePasswordForm(ChangePasswordForm changePasswordForm) {
        this.changePasswordForm = changePasswordForm;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public boolean isChangingSucceed() {
        return changingSucceed;
    }

    public void setChangingSucceed(boolean changingSucceed) {
        this.changingSucceed = changingSucceed;
    }
}
