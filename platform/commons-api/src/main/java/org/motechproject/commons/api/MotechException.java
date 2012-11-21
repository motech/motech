package org.motechproject.commons.api;

public class MotechException extends RuntimeException {
    public MotechException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public MotechException(String message) {
        super(message);
    }
}
