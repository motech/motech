package org.motechproject.mds.ex;

/**
 * Singlans an error when executing the exception
 */
public class LookupExecutorException extends RuntimeException {
    private static final long serialVersionUID = -1676839116204380821L;

    public LookupExecutorException(String message, Throwable cause) {
        super(message, cause);
    }
}
