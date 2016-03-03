package org.motechproject.mds.ex.rest;

import org.motechproject.mds.ex.MdsException;

/**
 * Signals an internal issue with the REST support.
 */
public class RestInternalException extends MdsException {

    private static final long serialVersionUID = -2546647662938818140L;

    public RestInternalException(String message) {
        super(message);
    }

    public RestInternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
