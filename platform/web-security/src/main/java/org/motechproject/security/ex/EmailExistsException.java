package org.motechproject.security.ex;

public class EmailExistsException extends RuntimeException {

    public EmailExistsException() {
    }

    public EmailExistsException(String message) {
        super(message);
    }
}
