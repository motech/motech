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

public class ExternalIdCriterionTest {

    @Test
    public void shouldFilterByExternalId() {
        Schedule schedule = new Schedule("some_schedule");
        List<Enrollment> allEnrollments = new ArrayList<Enrollment>();
        allEnrollments.add(new Enrollment("someExternalId", schedule, null, null, null, null, null));
        allEnrollments.add(new Enrollment("someOtherExternalId", schedule, null, null, null, null, null));
        allEnrollments.add(new Enrollment("oneMoreExternalId", schedule, null, null, null, null, null));
        allEnrollments.add(new Enrollment("someExternalId", schedule, null, null, null, null, null));

        List<Enrollment> filteredEnrollments = new ExternalIdCriterion("someExternalId").filter(allEnrollments, null);
        assertEquals(asList(new String[]{"someExternalId", "someExternalId"}), extract(filteredEnrollments, on(Enrollment.class).getExternalId()));
    }
}
