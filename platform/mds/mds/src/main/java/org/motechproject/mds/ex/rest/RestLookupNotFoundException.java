package org.motechproject.mds.ex.rest;

/**
 * Signals that the lookup requested by REST does not exist.
 */
public class RestLookupNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 5777555133575169106L;

    public RestLookupNotFoundException(String lookupName) {
        super(String.format("Lookup \'%s\' not found", lookupName));
    }
}
