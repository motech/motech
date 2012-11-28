package org.motechproject.mrs.exception;

public class MRSException extends RuntimeException {
    public MRSException(Throwable e) {
        super(e);
    }

    public MRSException(String message) {
        super(message);
    }

    public MRSException(String message, Throwable cause) {
        super(message, cause);
    }
}
