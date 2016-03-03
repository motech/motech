package org.motechproject.mds.exception.lookup;

/**
 * Signals that a collection result was expected, but the lookup returned a single result.
 */
public class CollectionResultFromLookupExpectedException extends IllegalLookupReturnTypeException {

    private static final long serialVersionUID = 9104274217730361890L;

    /**
     * @param lookupName name of the lookup
     */
    public CollectionResultFromLookupExpectedException(String lookupName) {
        super(lookupName + " returned a single item, a collection of results was expected");
    }
}
