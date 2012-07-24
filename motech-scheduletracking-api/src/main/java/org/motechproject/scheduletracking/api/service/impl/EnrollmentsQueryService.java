package org.motechproject.scheduletracking.api.service.impl;

import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.search.Criterion;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EnrollmentsQueryService {

    private EnrollmentService enrollmentService;
    private AllEnrollments allEnrollments;

    @Autowired
    public EnrollmentsQueryService(AllEnrollments allEnrollments, EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
        this.allEnrollments = allEnrollments;
    }

    public List<Enrollment> search(EnrollmentsQuery query) {
        List<Enrollment> enrollments = new ArrayList<Enrollment>();
        Criterion primaryCriterion = query.getPrimaryCriterion();
        if (primaryCriterion != null) {
            enrollments = primaryCriterion.fetch(allEnrollments, enrollmentService);
        }
        for (Criterion criterion : query.getSecondaryCriteria()) {
            enrollments = criterion.filter(enrollments, enrollmentService);
        }
        return enrollments;
    }
}
