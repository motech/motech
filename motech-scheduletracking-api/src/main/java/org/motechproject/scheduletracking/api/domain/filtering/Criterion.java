package org.motechproject.scheduletracking.api.domain.filtering;

import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentService;

import java.util.List;

public interface Criterion  {
    public abstract List<Enrollment> fetch(AllEnrollments allEnrollments);
    public abstract List<Enrollment> filter(List<Enrollment> enrollments, EnrollmentService enrollmentService);
}


