package org.motechproject.osgi.web.exception;

/**
 * Thrown when an error occurs during registration of a module servlet.
 */
public class ServletRegistrationException extends RuntimeException {

    private static final long serialVersionUID = 4521822242529427008L;

    public ServletRegistrationException(String message) {
        super(message);
    }

    public ServletRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServletRegistrationException(Throwable cause) {
        super(cause);
    }
}
