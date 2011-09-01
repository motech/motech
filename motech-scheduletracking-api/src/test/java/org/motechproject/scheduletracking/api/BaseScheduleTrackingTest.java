package org.motechproject.scheduletracking.api;

import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

public abstract class BaseScheduleTrackingTest {
    protected LocalDate daysAgo(int numberOfDays) {
        return LocalDate.now().minusDays(numberOfDays);
    }

    protected LocalDate weeksAgo(int numberOfWeeks) {
        return LocalDate.now().minusWeeks(numberOfWeeks);
    }

    protected Enrollment enrollment(int daysAgo) {
        return new Enrollment("1234", LocalDate.now().minusDays(daysAgo), "S1");
    }

    protected WallTime wallTimeOf(int numberOfWeeks) {
        return new WallTime(numberOfWeeks, WallTimeUnit.Week);
    }

    protected Schedule createSchedule() {
        Milestone secondShot = new Milestone("Second Shot", wallTimeOf(11), wallTimeOf(12), wallTimeOf(13), wallTimeOf(14));
        Milestone firstShot = new Milestone("First Shot", secondShot, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        return new Schedule("Yellow Fever Vaccination", wallTimeOf(52), firstShot);
    }
}
