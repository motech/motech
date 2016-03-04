package org.motechproject.commons.date.exception;

/**
 * Signals a problem with parsing dates or periods.
 */
public class ParseException extends RuntimeException {

    private static final long serialVersionUID = 4183931135283563100L;

    public ParseException(String message) {
        super(message);
    }
}
