package org.motechproject.scheduletracking.api.service.contract;

import java.util.HashMap;
import java.util.Map;
/**
 * \defgroup sts Schedule Tracking Service
 */
/**
 * \ingroup sts
 * This is the criteria builder which is used to form an enrollment update criteria
 */
public class UpdateCriteria {

    private Map<UpdateCriterion, Object> allCriteria = new HashMap<UpdateCriterion, Object>();

    /**
     * This gives the list of all the criterion specified to update an enrollment
     * @return
     */
    public Map<UpdateCriterion, Object> getAll() {
        return allCriteria;
    }

    /** Adds metadata criteria to the update criteria list
     *
     * @param metadata value to be updated in the enrollment
     * @return returns the instance with metadata criteria added to the criteria list
     */
    public UpdateCriteria metadata(HashMap<String, String> metadata) {
        allCriteria.put(UpdateCriterion.Metadata, metadata);
        return this;
    }

}
