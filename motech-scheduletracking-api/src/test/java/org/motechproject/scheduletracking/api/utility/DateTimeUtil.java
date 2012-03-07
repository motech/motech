package org.motechproject.scheduletracking.api.utility;

import org.joda.time.DateTime;

import static org.motechproject.util.DateUtil.now;

public final class DateTimeUtil {
    private DateTimeUtil() {
    }

    public static DateTime daysAgo(int numberOfDays) {
        return now().minusDays(numberOfDays);
    }

    public static DateTime weeksAgo(int numberOfWeeks) {
        return now().minusWeeks(numberOfWeeks);
    }

    public static DateTime yearsAgo(int numberOfYears) {
        return now().minusYears(numberOfYears);
    }

    public static DateTime daysAfter(int numberOfDays) {
        return now().plusDays(numberOfDays);
    }

    public static DateTime weeksAfter(int numberOfWeeks) {
        return now().plusWeeks(numberOfWeeks);
    }
}
