package org.motechproject.scheduletracking.api.service.impl;

import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.filtering.Criterion;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EnrollmentsQueryService {

    private EnrollmentService enrollmentService;
    private AllEnrollments allEnrollments;

    @Autowired
    public EnrollmentsQueryService(EnrollmentService enrollmentService, AllEnrollments allEnrollments) {
        this.enrollmentService = enrollmentService;
        this.allEnrollments = allEnrollments;
    }

    public List<Enrollment> search(EnrollmentsQuery query) {
        List<Enrollment> enrollments = allEnrollments.getAll();
        for (Criterion criterion : query.getCriteria()) {
            enrollments = criterion.filter(enrollments, enrollmentService);
        }
        return enrollments;
    }
}
