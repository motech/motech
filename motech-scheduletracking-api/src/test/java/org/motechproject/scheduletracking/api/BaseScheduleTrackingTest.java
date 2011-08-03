package org.motechproject.scheduletracking.api;

import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.domain.enrolment.Enrolment;

public abstract class BaseScheduleTrackingTest {
    protected int daysAgo(int numberOfDays) {
        return numberOfDays;
    }

    protected Enrolment enrolment(int daysAgo) {
        return new Enrolment("1234", LocalDate.now().minusDays(daysAgo), "S1");
    }
}
