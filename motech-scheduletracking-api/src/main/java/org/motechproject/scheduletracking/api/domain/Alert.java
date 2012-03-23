package org.motechproject.scheduletracking.api.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;
import org.joda.time.Period;

import static java.lang.Math.ceil;
import static java.lang.Math.min;
import static org.joda.time.Days.daysBetween;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.now;

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

    int getElapsedAlertCount(DateTime startReferenceDateTime, Time preferredAlertTime) {
        DateTime idealStartDateTime = startReferenceDateTime.plus(offset);
        DateTime idealStartDateWithPreferredTime = idealStartDateTime;
        if (preferredAlertTime != null)
            idealStartDateWithPreferredTime = DateUtil.newDateTime(idealStartDateTime.toLocalDate(), preferredAlertTime.getHour(), preferredAlertTime.getMinute(), 0);

        DateTime now = now();
        if (idealStartDateWithPreferredTime.isBefore(now)) {
            long secsSinceIdealStartOfAlert = (now.getMillis() - idealStartDateWithPreferredTime.getMillis()) / 1000;
            int elapsedAlerts = (int) ceil(secsSinceIdealStartOfAlert / (double) interval.toStandardSeconds().getSeconds());
            return min(elapsedAlerts, count);
        }
        return 0;
    }

    public DateTime getNextAlertDateTime(DateTime startReferenceDateTime, Time preferredAlertTime) {
        DateTime idealStartDateTime = startReferenceDateTime.plus(offset);
        DateTime nextAlertDateTime = idealStartDateTime.plusDays(getElapsedAlertCount(startReferenceDateTime, preferredAlertTime) * interval.toStandardDays().getDays());
        if (preferredAlertTime != null)
            return newDateTime(nextAlertDateTime.toLocalDate(), preferredAlertTime.getHour(), preferredAlertTime.getMinute(), 0);
        return nextAlertDateTime;
    }

    public int getRemainingAlertCount(DateTime milestoneWindowStartDateTime, Time preferredAlertTime) {
        return count - getElapsedAlertCount(milestoneWindowStartDateTime,  preferredAlertTime);
    }
}
