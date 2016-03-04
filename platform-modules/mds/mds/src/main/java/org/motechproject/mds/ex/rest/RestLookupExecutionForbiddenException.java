package org.motechproject.mds.ex.rest;

import org.motechproject.mds.ex.MdsException;

/**
 * Signals that the lookup can not be executed through REST
 * since it is not exposed. Thrown only for existing lookups
 * that are not exposed.
 */
public class RestLookupExecutionForbiddenException extends MdsException {

    private static final long serialVersionUID = 3202146110079162061L;

    /**
     * @param lookupName the name of the lookup
     */
    public RestLookupExecutionForbiddenException(String lookupName) {
        super(String.format("Lookup %s is not exposed in the REST API", lookupName));
    }
}
