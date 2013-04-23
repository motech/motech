package org.motechproject.commcare.exception;

public class FullFormParserException extends Exception {

    public FullFormParserException(String message) {
        super(message);
    }

    public FullFormParserException(Exception ex, String message) {
        super(message, ex);
    }
}
