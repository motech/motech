package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

public abstract class BaseScheduleTrackingTest {
	protected Milestone firstMilestone;
	protected Milestone secondMilestone;

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
	    secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
	    firstMilestone = new Milestone("First Shot", secondMilestone, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        return new Schedule("Yellow Fever Vaccination", wallTimeOf(52), firstMilestone);
    }
}
