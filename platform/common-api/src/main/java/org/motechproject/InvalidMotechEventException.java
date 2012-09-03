package org.motechproject;

public class InvalidMotechEventException extends RuntimeException {
    public InvalidMotechEventException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InvalidMotechEventException(String message) {
        super(message);
    }
}
