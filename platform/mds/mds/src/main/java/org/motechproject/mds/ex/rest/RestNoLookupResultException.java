package org.motechproject.mds.ex.rest;

/**
 * Thrown when there was no result for a single-value lookup.
 */
public class RestNoLookupResultException extends RuntimeException {

    private static final long serialVersionUID = -7954855446568904209L;

    public RestNoLookupResultException(String message) {
        super(message);
    }
}
