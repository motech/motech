package org.motechproject.osgi.web;

public class ServletRegistrationException extends RuntimeException {

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
