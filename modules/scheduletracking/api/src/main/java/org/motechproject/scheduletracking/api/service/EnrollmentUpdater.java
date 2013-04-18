package org.motechproject.scheduletracking.api.service;

import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.service.contract.UpdateCriterion;

import java.util.HashMap;
import java.util.Map;

public enum EnrollmentUpdater {
    metadataUpdater {
        @Override
        public Enrollment update(Enrollment enrollment, Object newValue) {
            Map<String, String> metadata = new HashMap<String, String>(enrollment.getMetadata());
            Map<String, String> tobeUpdatedMetadata = (Map<String, String>) newValue;
            
            for (Map.Entry<String,String> entry : tobeUpdatedMetadata.entrySet()) {
                metadata.put(entry.getKey(), entry.getValue());
            }

            enrollment.setMetadata(metadata);
            return enrollment;
        }
    };

    public abstract Enrollment update(Enrollment enrollment, Object newValue);

    private static Map<UpdateCriterion, EnrollmentUpdater> updateCriterionMap = new HashMap<UpdateCriterion, EnrollmentUpdater>();

    static {
        updateCriterionMap.put(UpdateCriterion.Metadata, EnrollmentUpdater.metadataUpdater);
    }

    public static EnrollmentUpdater get(UpdateCriterion updateCriterion) {
        return updateCriterionMap.get(updateCriterion);
    }
}
