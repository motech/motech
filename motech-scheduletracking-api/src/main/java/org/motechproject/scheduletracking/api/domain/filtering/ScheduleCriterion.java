package org.motechproject.scheduletracking.api.domain.filtering;

import ch.lambdaj.Lambda;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentService;

import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;

public class ScheduleCriterion implements Criterion {

    private String scheduleName;

    public ScheduleCriterion(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    @Override
    public List<Enrollment> filter(List<Enrollment> enrollments, EnrollmentService enrollmentService) {
        return Lambda.filter(having(on(Enrollment.class).getScheduleName(), equalTo(scheduleName)), enrollments);
    }
}
