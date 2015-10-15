package org.motechproject.security.config;


import org.motechproject.security.validator.PasswordValidator;

/**
 * Utility service used by the web-security module for retrieving platform settings related to security.
 */
public interface SettingService {

    int DEFAULT_SESSION_TIMEOUT = 30 * 60; // 30 minutes

    /**
     * Returns whether providing an email is required for creating a user.
     *
     * @return true if the email is required, false otherwise
     */
    boolean getEmailRequired();

    /**
     * Gets the http session timeout for Motech users. Users will be logged out after reaching this timeout.
     * This value is specified in seconds. A negative value specifies that sessions should never time out.
     * If the session timeout is not configured or set to 0, the default timeout of 30 minutes (1800) is returned.
     *
     * @return the http session timeout, in seconds
     */
    int getSessionTimeout();

    /**
     * Retrieves a validator to be used for passwords. The validator name is set in the platform settings.
     *
     * @return the validator
     */
    PasswordValidator getPasswordValidator();

    /**
     * Retrieves the minimal password length. 0 or less no minimal length configured.
     *
     * @return the mimnimal password length
     */
    int getMinPasswordLength();

    /**
     * Returns failure login limit. After reaching this limit user will be blocked.
     *
     * @return the failure login limit
     */
    int getFailureLoginLimit();

    /**
     * Gets the number of days after which the user will have to change password.
     *
     * @return the number of days after to change password.
     */
    int getNumberOfDaysToChangePassword();

    /**
     * Checks whether the reminding about password reset is enabled.
     *
     * @return true when the reminding is enabled
     */
    boolean isPasswordResetReminderEnabled();

    /**
     * Gets the number of days before password expiration to send the reminder at.
     *
     * @return the number of days before password expiration to send the reminder at
     */
    int getNumberOfDaysForReminder();

}
