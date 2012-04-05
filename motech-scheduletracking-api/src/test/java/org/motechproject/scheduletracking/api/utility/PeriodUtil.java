package org.motechproject.scheduletracking.api.utility;

import org.joda.time.Period;

public class PeriodUtil {
    public static Period years(int numberOfYears) {
        return new Period(numberOfYears, 0, 0, 0, 0, 0, 0, 0);
    }

    public static Period months(int numberOfMonths) {
        return new Period(0, numberOfMonths, 0, 0, 0, 0, 0, 0);
    }

    public static Period weeks(int numberOfWeeks) {
        return new Period(0, 0, numberOfWeeks, 0, 0, 0, 0, 0);
    }

    public static Period days(int numberOfDays) {
        return new Period(0, 0, 0, numberOfDays, 0, 0, 0, 0);
    }

    public static Period hours(int numberOfHours) {
        return new Period(0, 0, 0, 0, numberOfHours, 0, 0, 0);
    }
}
