package org.motechproject.mds.ex.lookup;

/**
 * Signals that the lookup returned a Collection, while a single result was expected.
 */
public class SingleResultFromLookupExpectedException extends IllegalLookupReturnTypeException {
    private static final long serialVersionUID = -4003686233482373689L;

    public SingleResultFromLookupExpectedException(String lookupName) {
        super(lookupName + " returned a Collection, a single result was expected");
    }
}
