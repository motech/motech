package org.motechproject.mds.exception.object;

import org.motechproject.mds.exception.MdsException;

/**
 * The <code>SecurityException</code> exception signals a situation in which user wants
 * to perform an operation on objects, they don't have access to.
 */
public class SecurityException extends MdsException {

    private static final long serialVersionUID = 8955069598422625786L;

    private static final String MSG = "An attempt to access secured Entity or instance of such entity has been performed. " +
            "The access has been blocked due to insufficient credentials.";

    /**
     * Constructs a new SecurityException with <i>mds.error.securityError</i> as
     * a message key.
     */
    public SecurityException() {
        super(MSG, null, "mds.error.securityError");
    }
}
