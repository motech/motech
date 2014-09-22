package org.motechproject.mds.ex;

/**
 * Signals that we were unable to revert an object from trash.
 */
public class RevertFromTrashException extends RuntimeException {

    private static final long serialVersionUID = 2557636568987874398L;

    public RevertFromTrashException(String message) {
        super(message);
    }

    public RevertFromTrashException(String message, Throwable cause) {
        super(message, cause);
    }
}
