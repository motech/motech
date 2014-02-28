package org.motechproject.mds.ex;

/**
 * The <code>SecurityException</code> exception signals a situation in which user wants
 * to perform an operation on objects, they don't have access to.
 */
public class SecurityException extends MdsException {
    private static final long serialVersionUID = 8955069598422625786L;

    /**
     * Constructs a new SecurityException with <i>mds.error.securityError</i> as
     * a message key.
     */
    public SecurityException() {
        super("mds.error.securityError");
    }
}
