package org.motechproject.appointments.api.mapper;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.TypeOfVisit;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.util.DateUtil;

public class VisitMapper {

    public Visit mapScheduledVisit(int weekOffset, ReminderConfiguration reminderConfiguration) {
        DateTime scheduledDate = DateUtil.now().plusWeeks(weekOffset);
        Reminder appointmentReminder = new ReminderMapper().map(scheduledDate, reminderConfiguration);
        return new Visit().name("week" + weekOffset).typeOfVisit(TypeOfVisit.Scheduled).addAppointment(scheduledDate, appointmentReminder);
    }
}
