package org.motechproject.scheduletracking.api.domain.filtering;

import ch.lambdaj.Lambda;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentService;

import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;

public class StatusCriterion implements Criterion {

    private List<EnrollmentStatus> statuses;

    public StatusCriterion(List<EnrollmentStatus> statuses) {
        this.statuses = statuses;
    }

    @Override
    public List<Enrollment> filter(List<Enrollment> enrollments, EnrollmentService enrollmentService) {
        return Lambda.filter(having(on(Enrollment.class).getStatus(), isIn(statuses)), enrollments);
    }
}
