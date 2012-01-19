package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

public abstract class BaseScheduleTrackingTest {
	protected Milestone firstShot;
	protected Milestone secondShot;

	protected LocalDate daysAgo(int numberOfDays) {
        return LocalDate.now().minusDays(numberOfDays);
    }

    protected LocalDate weeksAgo(int numberOfWeeks) {
        return LocalDate.now().minusWeeks(numberOfWeeks);
    }

    protected WallTime wallTimeOf(int numberOfWeeks) {
        return new WallTime(numberOfWeeks, WallTimeUnit.Week);
    }

    protected Schedule createSchedule() {
	    secondShot = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
	    firstShot = new Milestone("First Shot", secondShot, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        return new Schedule("Yellow Fever Vaccination", wallTimeOf(52), firstShot);
    }
}
