package org.motechproject.mds.exception.rest;

import org.motechproject.mds.exception.MdsException;

/**
 * Signals that there were errors parsing the class
 * from the provided body.
 */
public class RestBadBodyFormatException extends MdsException {

    private static final long serialVersionUID = -7809212543496212240L;

    public RestBadBodyFormatException(String message) {
        super(message);
    }

    public RestBadBodyFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
