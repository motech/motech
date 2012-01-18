package org.motechproject.scheduletracking.api.domain;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.motechproject.valueobjects.WallTime;

import java.util.ArrayList;
import java.util.List;

public class MilestoneWindow {
    private WallTime begin;
    private WallTime end;

    private List<AlertConfiguration> alertConfigurations = new ArrayList<AlertConfiguration>();

    public MilestoneWindow(WallTime begin, WallTime end) {
        this.begin = begin;
        this.end = end;
    }

    public void addAlert(AlertConfiguration alertConfiguration) {
        alertConfigurations.add(alertConfiguration);
    }

    public boolean isApplicableTo(LocalDate enrolledDate) {
        LocalDate now = LocalDate.now();

        int daysElapsed = Days.daysBetween(enrolledDate, now).getDays();
        int startOnDay = toDays(begin.asPeriod());
        int endsOnDay = toDays(end.asPeriod());
        return daysElapsed >= startOnDay && daysElapsed <= endsOnDay;
    }

    private static int toDays(Period period) {
        return period.toStandardDays().getDays();
    }
}
