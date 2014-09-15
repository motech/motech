package org.motechproject.mds.ex;

/**
 * Signals that an error occurred during lookup execution.
 */
public class LookupExecutorException extends RuntimeException {

    private static final long serialVersionUID = -1676839116204380821L;

    public LookupExecutorException(String message, Throwable cause) {
        super(message, cause);
    }
}
