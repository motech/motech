package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.motechproject.scheduletracking.api.domain.enrolment.Enrolment;
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

    public boolean isApplicableTo(Enrolment enrolment) {
        LocalDate now = LocalDate.now();
        LocalDate enroledDate = enrolment.getEnroledDate();
        int daysElapsed = toDays(new Period(enroledDate, now));
        int startOnDay = toDays(begin.asPeriod());
        int endsOnDay = toDays(end.asPeriod());
        return daysElapsed >= startOnDay && daysElapsed <= endsOnDay;
    }

    private int toDays(Period period) {
        return period.toStandardDays().getDays();
    }
}
