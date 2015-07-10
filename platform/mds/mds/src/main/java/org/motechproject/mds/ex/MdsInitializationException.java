package org.motechproject.mds.ex;

/**
 * This exception singals an issue with starting MDS.
 */
public class MdsInitializationException extends RuntimeException {

    private static final long serialVersionUID = -8989684993748938643L;

    public MdsInitializationException(String message) {
        super(message);
    }

    public MdsInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
