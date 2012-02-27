package org.motechproject.appointments.api.mapper;

import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.contract.VisitRequest;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;

public class VisitMapper {

    public Visit map(String visitName, VisitRequest visitRequest) {
        ReminderConfiguration reminderConfiguration = visitRequest.getReminderConfiguration();
        Visit visit = new Visit().name(visitName);
        if (reminderConfiguration != null) {
            Reminder appointmentReminder = new ReminderMapper().map(visitRequest.getDueDate(), reminderConfiguration);
            visit.addAppointment(visitRequest.getDueDate(), appointmentReminder);
        }
        visit.addData(visitRequest.getData());
        return visit;
    }

}
