package org.motechproject.server.pillreminder.builder;

import org.motechproject.server.pillreminder.contract.ReminderRequest;
import org.motechproject.server.pillreminder.domain.Reminder;

public class ReminderBuilder {

    public Reminder createFrom(ReminderRequest reminderRequest) {
        return new Reminder(reminderRequest.getHour(),
                reminderRequest.getMinute(),
                reminderRequest.getRepeatSize(),
                reminderRequest.getRepeatInterval());
    }
}
