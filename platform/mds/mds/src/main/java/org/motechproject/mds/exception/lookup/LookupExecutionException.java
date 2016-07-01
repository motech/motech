package org.motechproject.mds.exception.lookup;

import org.motechproject.mds.exception.MdsException;

/**
 * Signals that it was not possible to execute a lookup for a given entity.
 */
public class LookupExecutionException extends MdsException {

    private static final long serialVersionUID = -2983280325274960321L;

    public LookupExecutionException(Throwable cause, String messageKey) {
        super("Execution of lookup failed", cause, messageKey);
    }
}
