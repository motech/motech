package org.motechproject.security.exception;

/**
 * Exception that signalizes that given token is invalid
 */
public class InvalidTokenException extends Exception {

    public InvalidTokenException() {
        super();
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}
