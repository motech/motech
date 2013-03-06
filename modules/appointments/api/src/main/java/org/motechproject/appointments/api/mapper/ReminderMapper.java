package org.motechproject.appointments.api.mapper;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.service.contract.ReminderConfiguration;
import org.motechproject.appointments.api.service.contract.ReminderConfiguration.IntervalUnit;

public class ReminderMapper {

    public Reminder map(DateTime dueDateTime, ReminderConfiguration reminderConfiguration) {
        DateTime dueDate = dueDateTime;
        DateTime startDate = dueDate.minusSeconds(reminderConfiguration.getRemindFrom());
        DateTime endDate = startDate.plusSeconds((int) (intervalSeconds(reminderConfiguration.getIntervalUnit(), reminderConfiguration.getIntervalCount()) * reminderConfiguration.getRepeatCount()));
        long intervalSeconds = intervalSeconds(reminderConfiguration.getIntervalUnit(), reminderConfiguration.getIntervalCount());
        return new Reminder().startDate(startDate.toDate()).endDate(endDate.toDate()).intervalSeconds(intervalSeconds).repeatCount(reminderConfiguration.getRepeatCount());
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
