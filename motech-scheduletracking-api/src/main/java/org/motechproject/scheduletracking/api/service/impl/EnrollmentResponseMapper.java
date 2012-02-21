package org.motechproject.scheduletracking.api.service.impl;

import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.service.EnrollmentResponse;

public class EnrollmentResponseMapper {

    public EnrollmentResponse map(Enrollment enrollment) {
        if(enrollment == null)
            return null;
        else
            return new EnrollmentResponse(enrollment.getExternalId(), enrollment.getScheduleName(),
                    enrollment.getPreferredAlertTime(), enrollment.getReferenceDate(), enrollment.getEnrollmentDate());
    }
}
