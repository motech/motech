package org.motechproject.security.ex;

/**
 * Exception that signalizes that given user is not an admin
 */
public class NonAdminUserException extends Exception {

    public NonAdminUserException(String message) {
        super(message);
    }
}
