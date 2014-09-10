package org.motechproject.mds.lookup;

import org.motechproject.mds.query.QueryParams;

import java.util.Map;

public interface LookupExecutor {

    Object execute(Map<String, Object> lookupMap);

    Object execute(Map<String, Object> lookupMap, QueryParams queryParams);

    long executeCount(Map<String, Object> lookupMap);
}
