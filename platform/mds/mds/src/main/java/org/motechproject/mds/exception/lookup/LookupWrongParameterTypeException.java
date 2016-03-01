package org.motechproject.mds.exception.lookup;

import org.motechproject.mds.exception.MdsException;

/**
 * Signals wrong type of lookup parameter
 */
public class LookupWrongParameterTypeException extends MdsException {

    private static final long serialVersionUID = -6938222110979370652L;

    public LookupWrongParameterTypeException(String message) {
        super(message);
    }
}
