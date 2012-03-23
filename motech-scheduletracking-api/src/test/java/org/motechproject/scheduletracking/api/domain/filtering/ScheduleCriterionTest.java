package org.motechproject.scheduletracking.api.domain.filtering;

import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Schedule;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

public class ScheduleCriterionTest {

    @Test
    public void shouldFilterByExternalId() {
        Schedule schedule = new Schedule("schedule_1");
        Schedule schedule2 = new Schedule("schedule_2");
        List<Enrollment> allEnrollments = new ArrayList<Enrollment>();
        allEnrollments.add(new Enrollment(null, schedule, null, null, null, null, null, null));
        allEnrollments.add(new Enrollment(null, schedule, null, null, null, null, null, null));
        allEnrollments.add(new Enrollment(null, schedule2, null, null, null, null, null, null));
        allEnrollments.add(new Enrollment(null, schedule2, null, null, null, null, null, null));

        List<Enrollment> filteredEnrollments = new ScheduleCriterion("schedule_1").filter(allEnrollments, null);
        assertEquals(asList(new String[]{"schedule_1", "schedule_1"}), extract(filteredEnrollments, on(Enrollment.class).getScheduleName()));
    }
}
