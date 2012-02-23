package org.motechproject.scheduletracking.api.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;

import static java.lang.Math.ceil;
import static java.lang.Math.min;
import static org.joda.time.Days.daysBetween;

public class Alert {

    private WallTime offset;
    private WallTime interval;
    private int count;
    private int index;

    public Alert(WallTime offset, WallTime interval, int count, int index) {
        this.offset = offset;
        this.interval = interval;
        this.count = count;
        this.index = index;
    }

    public WallTime getOffset() {
        return offset;
    }

    public WallTime getInterval() {
        return interval;
    }

    public int getCount() {
        return count;
    }

    public int getIndex() {
        return index;
    }

    public int getElapsedAlertCount(LocalDate startDate, Time preferredAlertTime) {
        LocalDate idealStartDate = startDate.plusDays(offset.inDays());
        DateTime idealStartDateWithTime = DateUtil.newDateTime(idealStartDate, preferredAlertTime.getHour(), preferredAlertTime.getMinute(), 0);

        int elapsedAlerts = 0;
        if (idealStartDateWithTime.isBefore(DateUtil.now())) {
            int daysSinceIdealStartOfAlert = daysBetween(idealStartDateWithTime, DateUtil.now()).getDays() + 1;
            elapsedAlerts = (int) ceil(daysSinceIdealStartOfAlert / (double) interval.inDays());
        }
        return min(elapsedAlerts, count);
    }

    public DateTime getNextAlertDateTime(LocalDate startDate, Time preferredAlertTime) {
        LocalDate idealStartDate = startDate.plusDays(offset.inDays());
        LocalDate nextAlertDate = idealStartDate.plusDays(getElapsedAlertCount(startDate, preferredAlertTime) * interval.inDays());
        return DateUtil.newDateTime(nextAlertDate, preferredAlertTime.getHour(), preferredAlertTime.getMinute(), 0);
    }

    public int getRemainingAlertCount(LocalDate milestoneWindowStartDate, Time preferredAlertTime) {
        return count - getElapsedAlertCount(milestoneWindowStartDate,  preferredAlertTime);
    }
}
