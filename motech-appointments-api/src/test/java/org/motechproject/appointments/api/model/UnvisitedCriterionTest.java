package org.motechproject.appointments.api.model;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.motechproject.util.DateUtil.newDateTime;

public class UnvisitedCriterionTest {

    @Test
    public void shouldFindVisitsWhichAreDueInTimeRange() {
        Visit visit1 = new Visit();
        Visit visit2 = new Visit().visitDate(newDateTime(2012, 1, 1, 0, 0, 0));
        Visit visit3 = new Visit().visitDate(newDateTime(2012, 1, 2, 0, 0, 0));
        Visit visit4 = new Visit();
        List<Visit> visits = asList(new Visit[]{visit1, visit2, visit3, visit4});

        assertEquals(asList(new Visit[]{visit1, visit4}), new UnvisitedCriterion().filter(visits));
    }

}
