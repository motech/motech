package org.motechproject.scheduletracking.api.service.contract;

import java.util.HashMap;
import java.util.Map;

public class UpdateCriteria {

    private Map<UpdateCriterion, Object> allCriteria = new HashMap<UpdateCriterion, Object>();

    public Map<UpdateCriterion, Object> getAll() {
        return allCriteria;
    }

    public UpdateCriteria Metadata(HashMap<String,String> metadata)
    {
        allCriteria.put(UpdateCriterion.Metadata,metadata);
        return this;
    }

}
