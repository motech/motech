package org.motechproject.osgi.web.exception;

/**
 * This exception is thrown when a problem occurs during bundle configuration loading
 */
public class BundleConfigurationLoadingException extends Exception {

    public BundleConfigurationLoadingException(String message) {
        super(message);
    }

    public BundleConfigurationLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public BundleConfigurationLoadingException(Throwable cause) {
        super(cause);
    }
}
