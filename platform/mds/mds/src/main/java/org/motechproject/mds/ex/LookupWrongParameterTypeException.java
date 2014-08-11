package org.motechproject.mds.ex;

/**
 * Signals wrong type of lookup parameter
 */
public class LookupWrongParameterTypeException extends RuntimeException {

    private static final long serialVersionUID = -6938222110979370652L;

    public LookupWrongParameterTypeException(String message) {
        super(message);
    }
}
