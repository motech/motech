package org.motechproject.scheduletracking.api;

import org.joda.time.LocalDate;
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
}
