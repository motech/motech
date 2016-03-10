package org.motechproject.security.exception;

/**
 * Represents a failed attempt to delete a role currently assigned to a user.
 */
public class RoleHasUserException extends RuntimeException {

    public RoleHasUserException(String message) {
        super(message);
    }
}
