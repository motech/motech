package org.motechproject.security.exception;

/**
 * Exception that signalizes that given user is not an admin
 */
public class NonAdminUserException extends Exception {

    public NonAdminUserException(String message) {
        super(message);
    }
}
