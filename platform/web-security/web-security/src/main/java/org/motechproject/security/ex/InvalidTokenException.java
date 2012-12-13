package org.motechproject.security.ex;

public class InvalidTokenException extends Exception {

    public InvalidTokenException() {
        super();
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}
