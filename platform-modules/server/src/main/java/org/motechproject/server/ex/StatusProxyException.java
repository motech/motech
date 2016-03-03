package org.motechproject.server.ex;

/**
 * Signals that an error occurred while retrieving the platform status.
 */
public class StatusProxyException extends RuntimeException {

    private static final long serialVersionUID = -3508619626464587468L;

    public StatusProxyException(String message) {
        super(message);
    }

    public StatusProxyException(String message, Throwable cause) {
        super(message, cause);
    }
}
