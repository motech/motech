package org.motechproject.appointments.api.mapper;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.TypeOfVisit;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.util.DateUtil;

public class VisitMapper {

    public Visit mapScheduledVisit(int weekOffset, ReminderConfiguration appointmentReminderConfiguration) {
        DateTime dueDate = DateUtil.now().plusWeeks(weekOffset);
        Reminder appointmentReminder = new ReminderMapper().map(dueDate, appointmentReminderConfiguration);
        return new Visit().weekNumber(weekOffset).typeOfVisit(TypeOfVisit.Scheduled).addAppointment(dueDate, appointmentReminder);
    }

    public Visit mapUnscheduledVisit(DateTime scheduledDate, ReminderConfiguration appointmentReminderConfiguration, TypeOfVisit typeOfVisit) {
        Reminder appointmentReminder = new ReminderMapper().map(scheduledDate, appointmentReminderConfiguration);
        return new Visit().name("visitFor-" + scheduledDate.getMillis()).typeOfVisit(typeOfVisit).addAppointment(scheduledDate, appointmentReminder);
    }
}
