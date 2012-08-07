package org.motechproject.scheduletracking.api.domain.search;

import ch.lambdaj.Lambda;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentService;

import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.isIn;

public class ScheduleCriterion implements Criterion {
    private List<String> scheduleNames;

    public ScheduleCriterion(String... scheduleNames) {
        this.scheduleNames = Arrays.asList(scheduleNames);
    }

    @Override
    public List<Enrollment> fetch(AllEnrollments allEnrollments, EnrollmentService enrollmentService) {
        return allEnrollments.findBySchedule(scheduleNames);
    }

    @Override
    public List<Enrollment> filter(List<Enrollment> enrollments, EnrollmentService enrollmentService) {
        return Lambda.filter(having(on(Enrollment.class).getScheduleName(), isIn(scheduleNames)), enrollments);
    }
}
