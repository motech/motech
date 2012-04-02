package org.motechproject.scheduletracking.api.service.contract;

import java.util.HashMap;
import java.util.Map;

public class UpdateCriteria {

    private Map<UpdateCriterion, Object> allCriteria = new HashMap<UpdateCriterion, Object>();

    public Map<UpdateCriterion, Object> getAll() {
        return allCriteria;
    }

    /** Adds metadata criteria to the update criteria list
     *
     * @param metadata value to be updated in the enrollment
     * @return returns the instance with metadata criteria added to the criteria list
     */
    public UpdateCriteria Metadata(HashMap<String,String> metadata)
    {
        allCriteria.put(UpdateCriterion.Metadata,metadata);
        return this;
    }

}
