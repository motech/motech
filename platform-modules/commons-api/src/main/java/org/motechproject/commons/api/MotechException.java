package org.motechproject.commons.api;

/**
 * A generic Runtime Exception used in MOTECH.
 */
public class MotechException extends RuntimeException {

    private static final long serialVersionUID = -5856046276164755410L;

    public MotechException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public MotechException(String message) {
        super(message);
    }
}
