package org.motechproject.mds.ex.rest;

/**
 * Signals that the given operation is not supported by the given entity.
 */
public class RestOperationNotSupportedException extends RuntimeException {

    private static final long serialVersionUID = 4860558376062652428L;

    public RestOperationNotSupportedException(String message) {
        super(message);
    }
}
