package org.motechproject.mds.exception.lookup;

import org.motechproject.mds.exception.MdsException;

/**
 * Signals that an error occurred during lookup execution.
 */
public class LookupExecutorException extends MdsException {

    private static final long serialVersionUID = -1676839116204380821L;

    public LookupExecutorException(String message, Throwable cause, String messageKey) {
        super(message, cause, messageKey);
    }
}
