package org.motechproject.scheduletracking.api.domain.enrollment;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EnrollmentTest {
    @Test
    public void shouldGetAlertsForTheUpcomingMilestone() {
        LocalDate dayBeforeYesterday = LocalDate.now().minusDays(2);
        Schedule schedule = new Schedule("foo", new WallTime(10, WallTimeUnit.Week));
        Milestone first = new Milestone("One", schedule,
                new WallTime(1, WallTimeUnit.Week), new WallTime(2, WallTimeUnit.Week),
                new WallTime(3, WallTimeUnit.Week), new WallTime(4, WallTimeUnit.Week));
        schedule.addMilestone(first);

        Enrollment enrollment = schedule.newEnrollment("1234", dayBeforeYesterday);

        assertThat(enrollment.alertsFor(schedule).isEmpty(), is(false));
    }
}
