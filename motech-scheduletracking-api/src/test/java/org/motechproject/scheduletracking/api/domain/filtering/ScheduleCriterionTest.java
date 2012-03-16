package org.motechproject.scheduletracking.api.domain.filtering;

import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.Enrollment;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

public class ScheduleCriterionTest {

    @Test
    public void shouldFilterByExternalId() {
        List<Enrollment> allEnrollments = new ArrayList<Enrollment>();
        allEnrollments.add(new Enrollment(null, "schedule_1", null, null, null, null, null, null));
        allEnrollments.add(new Enrollment(null, "schedule_2", null, null, null, null, null, null));
        allEnrollments.add(new Enrollment(null, "schedule_1", null, null, null, null, null, null));
        allEnrollments.add(new Enrollment(null, "schedule_2", null, null, null, null, null, null));

        List<Enrollment> filteredEnrollments = new ScheduleCriterion("schedule_1").filter(allEnrollments, null);
        assertEquals(asList(new String[]{"schedule_1", "schedule_1"}), extract(filteredEnrollments, on(Enrollment.class).getScheduleName()));
    }
}
