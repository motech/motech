package org.motechproject.scheduletracking.api.domain.filtering;

import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.Schedule;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

public class StatusCriterionTest {

    @Test
    public void shouldFilterByExternalId() {
        Schedule schedule = new Schedule("some_schedule");
        List<Enrollment> allEnrollments = new ArrayList<Enrollment>();
        allEnrollments.add(new Enrollment(null, schedule, null, null, null, null, EnrollmentStatus.COMPLETED));
        allEnrollments.add(new Enrollment(null, schedule, null, null, null, null, EnrollmentStatus.ACTIVE));
        allEnrollments.add(new Enrollment(null, schedule, null, null, null, null, EnrollmentStatus.DEFAULTED));
        allEnrollments.add(new Enrollment(null, schedule, null, null, null, null, EnrollmentStatus.ACTIVE));

        List<Enrollment> filteredEnrollments = new StatusCriterion(asList(new EnrollmentStatus[]{ EnrollmentStatus.ACTIVE, EnrollmentStatus.COMPLETED})).filter(allEnrollments, null);
        assertEquals(asList(new EnrollmentStatus[]{ EnrollmentStatus.COMPLETED, EnrollmentStatus.ACTIVE, EnrollmentStatus.ACTIVE}), extract(filteredEnrollments, on(Enrollment.class).getStatus()));
    }
}
