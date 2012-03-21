package org.motechproject.appointments.api.model;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.motechproject.util.DateUtil.newDateTime;

public class DueDateInCriterionTest {

    @Test
    public void shouldFindVisitsWhichAreDueInTimeRange() {
        Visit visit1 = new Visit().addAppointment(newDateTime(2011, 3, 5, 1, 2, 3), null);
        Visit visit2 = new Visit().addAppointment(newDateTime(2011, 5, 2, 23, 59, 59), null);
        Visit visit3 = new Visit().addAppointment(newDateTime(2011, 5, 3, 0, 0, 0), null);
        Visit visit4 = new Visit().addAppointment(newDateTime(2011, 7, 5, 1, 2, 3), null);
        Visit visit5 = new Visit().addAppointment(newDateTime(2011, 8, 3, 0, 0, 0), null);
        Visit visit6 = new Visit().addAppointment(newDateTime(2011, 8, 3, 0, 0, 3), null);
        List<Visit> visits = asList(new Visit[]{visit1, visit2, visit3, visit4, visit5, visit6});

        DateTime start = newDateTime(2011, 5, 3, 0, 0, 0);
        DateTime end = newDateTime(2011, 8, 3, 0, 0, 0);
        DueDateInCriterion dueDateInCriterion = new DueDateInCriterion(start, end);
        assertEquals(asList(new Visit[]{visit3, visit4, visit5}), dueDateInCriterion.filter(visits));
    }

}
