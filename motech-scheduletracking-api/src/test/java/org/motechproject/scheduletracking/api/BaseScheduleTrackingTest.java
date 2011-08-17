package org.motechproject.scheduletracking.api;

import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;

public abstract class BaseScheduleTrackingTest {
    protected int daysAgo(int numberOfDays) {
        return numberOfDays;
    }

    protected Enrollment enrollment(int daysAgo) {
        return new Enrollment("1234", LocalDate.now().minusDays(daysAgo), "S1");
    }
}
