package org.motechproject.email.exception;

/**
 * Signals an issues with sending an Email message
 */
public class EmailSendException extends Exception {

    private static final long serialVersionUID = -1028896957652368345L;

    public EmailSendException(String message) {
        super(message);
    }

    public EmailSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
