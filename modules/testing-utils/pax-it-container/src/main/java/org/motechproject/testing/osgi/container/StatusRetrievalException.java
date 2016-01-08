package org.motechproject.testing.osgi.container;

/**
 * Signals that we are unable to retrieve the platform status.
 */
public class StatusRetrievalException extends RuntimeException {
    private static final long serialVersionUID = 7055829477756796209L;

    public StatusRetrievalException(String message) {
        super(message);
    }

    public StatusRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
