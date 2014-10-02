package org.motechproject.mds.ex;

/**
 * Signals that a collection result was expected, but the lookup returned a single result.
 */
public class CollectionResultFromLookupExpectedException extends IllegalLookupReturnTypeException {

    private static final long serialVersionUID = 9104274217730361890L;

    public CollectionResultFromLookupExpectedException(String lookupName) {
        super(lookupName + " returned a single item, a collection of results was expected");
    }
}
