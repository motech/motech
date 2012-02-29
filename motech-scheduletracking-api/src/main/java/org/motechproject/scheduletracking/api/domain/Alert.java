package org.motechproject.scheduletracking.api.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;
import org.joda.time.Period;

import static java.lang.Math.ceil;
import static java.lang.Math.min;
import static org.joda.time.Days.daysBetween;

public class Alert {
    private Period offset;
    private Period interval;
    private int count;
    private int index;

    public Alert(Period offset, Period interval, int count, int index) {
        this.offset = offset;
        this.interval = interval;
        this.count = count;
        this.index = index;
    }

    public Period getOffset() {
        return offset;
    }

    public Period getInterval() {
        return interval;
    }

    public int getIndex() {
        return index;
    }

    public int getElapsedAlertCount(LocalDate startDate, Time preferredAlertTime) {
        LocalDate idealStartDate = startDate.plus(offset);
        DateTime idealStartDateWithTime = DateUtil.newDateTime(idealStartDate, preferredAlertTime.getHour(), preferredAlertTime.getMinute(), 0);

        int elapsedAlerts = 0;
        if (idealStartDateWithTime.isBefore(DateUtil.now())) {
            int daysSinceIdealStartOfAlert = daysBetween(idealStartDateWithTime, DateUtil.now()).getDays() + 1;
            elapsedAlerts = (int) ceil(daysSinceIdealStartOfAlert / (double) interval.toStandardDays().getDays());
        }
        return min(elapsedAlerts, count);
    }

    public DateTime getNextAlertDateTime(LocalDate startDate, Time preferredAlertTime) {
        LocalDate idealStartDate = startDate.plus(offset);
        LocalDate nextAlertDate = idealStartDate.plusDays(getElapsedAlertCount(startDate, preferredAlertTime) * interval.toStandardDays().getDays());
        return DateUtil.newDateTime(nextAlertDate, preferredAlertTime.getHour(), preferredAlertTime.getMinute(), 0);
    }

    public int getRemainingAlertCount(LocalDate milestoneWindowStartDate, Time preferredAlertTime) {
        return count - getElapsedAlertCount(milestoneWindowStartDate,  preferredAlertTime);
    }
}
