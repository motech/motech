package org.motechproject.scheduletracking.api.domain;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.motechproject.valueobjects.WallTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MilestoneWindow implements Serializable {

    private WindowName name;

    private WallTime begin;
    private WallTime end;

    private List<Alert> alerts = new ArrayList<Alert>();

    public MilestoneWindow(WindowName name, WallTime begin, WallTime end) {
        this.name = name;
        this.begin = begin;
        this.end = end;
    }

    public WindowName getName() {
        return name;
    }

    public void addAlert(Alert alert) {
        alerts.add(alert);
    }

    public boolean isApplicableTo(LocalDate enrollmentDate) {
        LocalDate now = LocalDate.now();
        int daysElapsed = Days.daysBetween(enrollmentDate, now).getDays();
        int startOnDay = toDays(begin.asPeriod());
        int endsOnDay = getWindowEndInDays();
        return daysElapsed >= startOnDay && daysElapsed < endsOnDay;
    }

	private int getWindowEndInDays() {
		return end == null ? toDays(begin.asPeriod()) + 1 : toDays(end.asPeriod());
	}

	private static int toDays(Period period) {
        return period.toStandardDays().getDays();
    }
}
