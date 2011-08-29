package org.motechproject.scheduletracking.api.domain.factory;

import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.contract.EnrollmentRequest;
import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;

public class EnrollmentFactory
{
    public static Enrollment newEnrolment(EnrollmentRequest enrollmentRequest) {
        return new Enrollment(enrollmentRequest.getExternalId(), LocalDate.now(), enrollmentRequest.getScheduleName());
    }
}
