package org.motechproject.mds.ex.rest;

/**
 * Signals that the lookup can not be executed through REST
 * since it is not exposes. Thrown only for existing lookups
 * that are not exposed.
 */
public class RestLookupExecutionForbbidenException extends RuntimeException {

    private static final long serialVersionUID = 3202146110079162061L;

    public RestLookupExecutionForbbidenException(String lookupName) {
        super(String.format("Lookup %s is not exposed in the REST API", lookupName));
    }
}
