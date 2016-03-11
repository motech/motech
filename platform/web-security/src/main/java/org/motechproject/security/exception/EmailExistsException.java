package org.motechproject.security.exception;

/**
 * Exception that signalizes that given email is already
 * used by other user
 */
public class EmailExistsException extends RuntimeException {

    public EmailExistsException(String message) {
        super(message);
    }
}
