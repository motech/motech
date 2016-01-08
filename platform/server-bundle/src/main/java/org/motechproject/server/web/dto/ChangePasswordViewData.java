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

    private boolean changeSucceded;

    private boolean userBlocked;

    public ChangePasswordViewData(ChangePasswordForm changePasswordForm) {
        this.changePasswordForm = changePasswordForm;
        this.errors = new ArrayList<>();
        this.changeSucceded = false;
        this.userBlocked = false;
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

    public boolean isChangeSucceded() {
        return changeSucceded;
    }

    public void setChangeSucceded(boolean changeSucceded) {
        this.changeSucceded = changeSucceded;
    }

    public boolean isUserBlocked() {
        return userBlocked;
    }

    public void setUserBlocked(boolean userBlocked) {
        this.userBlocked = userBlocked;
    }

}
