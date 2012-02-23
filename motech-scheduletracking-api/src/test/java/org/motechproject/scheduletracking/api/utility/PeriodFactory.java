package org.motechproject.scheduletracking.api.utility;

import org.joda.time.Period;

public class PeriodFactory {

	public static Period weeks(int numberOfWeeks) {
		return new Period(0, 0, numberOfWeeks, 0, 0, 0, 0, 0);
	}

	public static Period days(int numberOfDays) {
		return new Period(0, 0, 0, numberOfDays, 0, 0, 0, 0);
	}
}
