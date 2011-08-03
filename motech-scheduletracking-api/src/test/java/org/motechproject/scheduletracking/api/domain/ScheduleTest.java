package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.enrolment.Enrolment;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class ScheduleTest {
    @Test
    public void alertFor() {
        LocalDate dayBeforeYesterday = LocalDate.now().minusDays(2);
        Enrolment enrolment = new Enrolment("1234", dayBeforeYesterday, "foo");
        Schedule schedule = new Schedule("foo", new WallTime(10, WallTimeUnit.Week));
        schedule.addMilestone(new Milestone("One", schedule, new WallTime(1, WallTimeUnit.Week), new WallTime(2, WallTimeUnit.Week),
                                                    new WallTime(3, WallTimeUnit.Week), new WallTime(4, WallTimeUnit.Week)));
        assertNotNull(schedule.alertFor(enrolment));
    }
}
