package org.motechproject.mds.rest;

import org.motechproject.mds.ex.rest.RestLookupExecutionForbbidenException;
import org.motechproject.mds.lookup.LookupExecutor;
import org.motechproject.mds.query.QueryParams;

import java.util.Map;

/**
 * Created by pawel on 9/10/14.
 */
public class ForbiddenLookupExecutor implements LookupExecutor {

    private final String lookupName;

    public ForbiddenLookupExecutor(String lookupName) {
        this.lookupName = lookupName;
    }

    @Override
    public Object execute(Map<String, Object> lookupMap) {
        throw new RestLookupExecutionForbbidenException(lookupName);
    }

    @Override
    public Object execute(Map<String, Object> lookupMap, QueryParams queryParams) {
        throw new RestLookupExecutionForbbidenException(lookupName);
    }

    @Override
    public long executeCount(Map<String, Object> lookupMap) {
        throw new RestLookupExecutionForbbidenException(lookupName);
    }
}
