package org.motechproject.scheduletracking.api.domain;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;

import static java.lang.Math.ceil;
import static java.lang.Math.min;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.now;

public class Alert {
    private Period offset;
    private Period interval;
    private int count;
    private int index;
    private boolean floating;

    public Alert(Period offset, Period interval, int count, int index, boolean floating) {
        this.offset = offset;
        this.interval = interval;
        this.count = count;
        this.index = index;
        this.floating = floating;
    }

    public int getCount() {
        return count;
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

    public boolean isFloating() {
        return floating;
    }

    int getElapsedAlertCount(DateTime startReferenceDateTime, Time preferredAlertTime) {
        DateTime idealStartDateWithPreferredTime = preferredAlertDateTime(startReferenceDateTime, preferredAlertTime);

        DateTime now = now();
        if (idealStartDateWithPreferredTime.isBefore(now)) {
            long secsSinceIdealStartOfAlert = (now.getMillis() - idealStartDateWithPreferredTime.getMillis()) / 1000;
            int elapsedAlerts = possibleNumbersOfAlertsInDuration(secsSinceIdealStartOfAlert);
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

    public int getRemainingAlertCount(DateTime startTimeForAlerts, DateTime windowEndTime, Time preferredAlertTime) {
        return min(count - getElapsedAlertCount(startTimeForAlerts, preferredAlertTime), maximumPossibleAlertsCount(startTimeForAlerts, windowEndTime, preferredAlertTime));
    }

    public void setOffset(Period offset) {
        this.offset = offset;
    }

    private int maximumPossibleAlertsCount(DateTime startTimeForAlerts, DateTime windowEndTime, Time preferredAlertTime) {
        DateTime preferredStartTimeForAlerts = preferredAlertDateTime(startTimeForAlerts, preferredAlertTime);
        long windowForAlerts = windowEndTime.minus(preferredStartTimeForAlerts.getMillis()).getMillis() / 1000;
        return possibleNumbersOfAlertsInDuration(windowForAlerts);
    }

    private int possibleNumbersOfAlertsInDuration(long duration) {
        return (int) ceil(duration / (double) interval.toStandardSeconds().getSeconds());
    }

    private DateTime preferredAlertDateTime(DateTime startReferenceDateTime, Time preferredAlertTime) {
        DateTime idealStartDateTime = startReferenceDateTime.plus(offset);
        DateTime idealStartDateWithPreferredTime = idealStartDateTime;
        if (preferredAlertTime != null)
            idealStartDateWithPreferredTime = DateUtil.newDateTime(idealStartDateTime.toLocalDate(), preferredAlertTime.getHour(), preferredAlertTime.getMinute(), 0);
        return idealStartDateWithPreferredTime;
    }
}
