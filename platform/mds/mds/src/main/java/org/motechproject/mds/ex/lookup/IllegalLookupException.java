package org.motechproject.mds.ex.lookup;

/**
 * Signals that the user defined an illegal lookup.
 */
public class IllegalLookupException extends RuntimeException {

    private static final long serialVersionUID = 5054278507284268561L;

    public IllegalLookupException(String message) {
        super(message);
    }
}
