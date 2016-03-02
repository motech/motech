package org.motechproject.mds.exception.object;

import org.motechproject.mds.exception.MdsException;

/**
 * Thrown when there was a problem with property creation.
 */
public class PropertyCreationException extends MdsException {

    private static final long serialVersionUID = 7421929879285823353L;

    public PropertyCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
