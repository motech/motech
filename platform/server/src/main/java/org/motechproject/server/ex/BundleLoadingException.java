package org.motechproject.server.ex;

/**
 * This exception is thrown when a problem occurs during bundle loading.
 */
public class BundleLoadingException extends Exception {

    public BundleLoadingException(String message) {
        super(message);
    }

    public BundleLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public BundleLoadingException(Throwable cause) {
        super(cause);
    }
}
