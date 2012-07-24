package org.motechproject.scheduletracking.api.domain.search;

import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentService;

import java.util.ArrayList;
import java.util.List;

public class MetadataCriterion implements Criterion {
    private String key;
    private String value;

    public MetadataCriterion(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public List<Enrollment> fetch(AllEnrollments allEnrollments, EnrollmentService enrollmentService) {
        return allEnrollments.findByMetadataProperty(key, value);
    }

    @Override
    public List<Enrollment> filter(List<Enrollment> enrollments, EnrollmentService enrollmentService) {
        List<Enrollment> filteredEnrollments = new ArrayList<Enrollment>();
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getMetadata() != null && value.equals(enrollment.getMetadata().get(key))) {
                filteredEnrollments.add(enrollment);
            }
        }
        return filteredEnrollments;
    }


}
