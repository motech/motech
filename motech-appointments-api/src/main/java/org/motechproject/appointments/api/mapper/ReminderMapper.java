package org.motechproject.appointments.api.mapper;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.contract.ReminderConfiguration.IntervalUnit;
import org.motechproject.appointments.api.model.Reminder;

import java.util.Date;

public class ReminderMapper {

    public Reminder map(DateTime scheduledDate, ReminderConfiguration reminderConfiguration) {
        Date startDate = scheduledDate.toLocalDate().minusDays(reminderConfiguration.getRemindFrom()).toDate();
        Date endDate = scheduledDate.toLocalDate().minusDays(reminderConfiguration.getRemindTill()).toDate();
        long intervalSeconds = intervalSeconds(reminderConfiguration.getIntervalUnit(), reminderConfiguration.getIntervalCount());
        return new Reminder().startDate(startDate).endDate(endDate).intervalSeconds(intervalSeconds).repeatCount(reminderConfiguration.getRepeatCount());
    }

    long intervalSeconds(IntervalUnit intervalUnit, int intervalCount) {
        if (IntervalUnit.SECONDS == intervalUnit) {
            return intervalCount;
        }
        if (IntervalUnit.MINUTES == intervalUnit) {
            return (intervalCount * 60);
        }
        if (ReminderConfiguration.IntervalUnit.HOURS == intervalUnit) {
            return (intervalCount * 60 * 60);
        }
        if (ReminderConfiguration.IntervalUnit.DAYS == intervalUnit) {
            return (intervalCount * 60 * 60 * 24);
        }
        if (IntervalUnit.WEEKS == intervalUnit) {
            return (intervalCount * 60 * 60 * 24 * 7);
        }
        return (long) -1;
    }
}
