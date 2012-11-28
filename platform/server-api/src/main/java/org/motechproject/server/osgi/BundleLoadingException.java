package org.motechproject.server.osgi;

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
