package org.motechproject.security.domain;

/**
 * Represents the user status.
 */
public enum UserStatus {

    /**
     * User is active.
     */
    ACTIVE,

    /**
     * User is active but he must change the password.
     */
    MUST_CHANGE_PASSWORD,

    /**
     * User is blocked.
     */
    BLOCKED
}
