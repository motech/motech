package org.motechproject.mds.exception;

/**
 * This exception singals an issue with starting MDS.
 */
public class MdsInitializationException extends MdsException {

    private static final long serialVersionUID = -8989684993748938643L;

    public MdsInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
