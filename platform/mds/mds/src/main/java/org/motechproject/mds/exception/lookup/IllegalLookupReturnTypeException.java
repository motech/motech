package org.motechproject.mds.exception.lookup;

import org.motechproject.mds.exception.MdsException;

/**
 * Signals that the exception returns one object but we expected a list(or vice-versa).
 */
public abstract class IllegalLookupReturnTypeException extends MdsException {

    private static final long serialVersionUID = 7824657066390516150L;

    public IllegalLookupReturnTypeException(String message) {
        super(message);
    }
}
