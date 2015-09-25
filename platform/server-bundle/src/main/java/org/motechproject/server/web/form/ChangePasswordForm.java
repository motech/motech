package org.motechproject.server.web.form;

import static org.apache.commons.lang.StringUtils.EMPTY;

/**
 * Represents data from the change password form from the UI.
 */
public class ChangePasswordForm {

    private String oldPassword;

    private String password;

    private String passwordConfirmation;

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     * Sets empty string to {@code oldPassword}, {@code password} and {@code passwordConfirmation} attributes.
     */
    public void resetPasswords() {
        this.oldPassword = EMPTY;
        this.password = EMPTY;
        this.passwordConfirmation = EMPTY;
    }
}
