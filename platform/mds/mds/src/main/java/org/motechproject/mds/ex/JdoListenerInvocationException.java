package org.motechproject.mds.ex;

public class JdoListenerInvocationException extends RuntimeException {
    private static final long serialVersionUID = -2504841346403362564L;

    public JdoListenerInvocationException(String message) {
        super(message);
    }

    public JdoListenerInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
