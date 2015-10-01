package org.motechproject.security.constants;

/**
 * Utility class used for storing event subjects used by the security module.
 */
public final class EventSubjects {

    public static final String PASSWORD_EXPIRATION_CHECK = "CheckPasswordExpiration";
    public static final String PASSWORD_CHANGE_REMINDER = "PasswordChangeReminder";

    /**
     * Utility class, should not be initiated.
     */
    private EventSubjects() {
    }
}
