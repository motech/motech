package org.motechproject.commcare.exception;

public class CommcareAuthenticationException extends Exception {

    public CommcareAuthenticationException(String message) {
        super(message);
    }

    public CommcareAuthenticationException(Exception ex, String message) {
        super(message, ex);
    }
}
