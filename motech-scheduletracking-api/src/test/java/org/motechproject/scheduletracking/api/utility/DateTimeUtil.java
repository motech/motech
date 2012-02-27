package org.motechproject.scheduletracking.api.utility;

import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

public final class DateTimeUtil {
    private DateTimeUtil() {
    }

    public static LocalDate daysAgo(int numberOfDays) {
        return DateUtil.today().minusDays(numberOfDays);
    }

    public static LocalDate weeksAgo(int numberOfWeeks) {
        return DateUtil.today().minusWeeks(numberOfWeeks);
    }

    public static LocalDate daysAfter(int numberOfDays) {
        return DateUtil.today().plusDays(numberOfDays);
    }

    public static LocalDate weeksAfter(int numberOfWeeks) {
        return DateUtil.today().plusWeeks(numberOfWeeks);
    }

    public static WallTime wallTimeInWeeks(int numberOfWeeks) {
        return new WallTime(numberOfWeeks, WallTimeUnit.Week);
    }

    public static WallTime wallTimeInDays(int numberOfDays) {
        return new WallTime(numberOfDays, WallTimeUnit.Day);
    }
}
