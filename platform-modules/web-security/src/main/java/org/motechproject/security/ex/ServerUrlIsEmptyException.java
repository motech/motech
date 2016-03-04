package org.motechproject.security.ex;

/**
 * Exception which signalizes that server url property in platform settings is empty
 */
public class ServerUrlIsEmptyException extends RuntimeException {
    private static final long serialVersionUID = -6704453759425844235L;

    public ServerUrlIsEmptyException() {
        super();
    }

    public ServerUrlIsEmptyException(String message) {
        super(message);
    }
}
