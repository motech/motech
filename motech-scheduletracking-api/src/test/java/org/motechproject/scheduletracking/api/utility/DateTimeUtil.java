package org.motechproject.scheduletracking.api.utility;

import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;

public class DateTimeUtil {
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
}
