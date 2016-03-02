package org.motechproject.mds.exception.object;

import org.motechproject.mds.exception.MdsException;

/**
 * Thrown when there was a problem with property creation.
 */
public class PropertyReadException extends MdsException {

    private static final long serialVersionUID = 212286569748750001L;

    public PropertyReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
