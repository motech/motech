package org.motechproject.commcare.exception;

public class CommcareConnectionFailureException extends Exception {

    public CommcareConnectionFailureException(String message) {
        super(message);
    }

    public CommcareConnectionFailureException(Exception ex, String message) {
        super(message, ex);
    }
}
