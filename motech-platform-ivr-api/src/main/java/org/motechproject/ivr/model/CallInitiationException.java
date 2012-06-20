package org.motechproject.ivr.model;

/**
 * Thrown when IVR can not initiate phone call due to some issue
 */
public class CallInitiationException extends RuntimeException {

    public CallInitiationException(String message) {
        super(message);
    }

    public CallInitiationException(String message, Throwable cause) {
        super(message, cause);
    }
}
