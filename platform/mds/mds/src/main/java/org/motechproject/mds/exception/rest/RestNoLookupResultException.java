package org.motechproject.mds.exception.rest;

import org.motechproject.mds.exception.MdsException;

/**
 * Thrown when there was no result for a single-value lookup.
 */
public class RestNoLookupResultException extends MdsException {

    private static final long serialVersionUID = -7954855446568904209L;

    public RestNoLookupResultException(String message) {
        super(message);
    }
}
