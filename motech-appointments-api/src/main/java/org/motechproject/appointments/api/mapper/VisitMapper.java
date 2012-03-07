package org.motechproject.appointments.api.mapper;

import org.motechproject.appointments.api.contract.CreateVisitRequest;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;

import java.util.ArrayList;
import java.util.List;

public class VisitMapper {

    public Visit map(CreateVisitRequest createVisitRequest) {
        Visit visit = new Visit().name(createVisitRequest.getVisitName()).typeOfVisit(createVisitRequest.getTypeOfVisit());
        createAppointment(createVisitRequest, visit);
        visit.addData(createVisitRequest.getData());
        return visit;
    }

    private void createAppointment(CreateVisitRequest createVisitRequest, Visit visit) {
        List<ReminderConfiguration> appointmentReminderConfigurations = createVisitRequest.getAppointmentReminderConfigurations();
        List<Reminder> appointmentReminders = null;
        if (appointmentReminderConfigurations != null) {
            appointmentReminders = new ArrayList<Reminder>();
            for (ReminderConfiguration appointmentReminderConfiguration : appointmentReminderConfigurations) {
                appointmentReminders.add(new ReminderMapper().map(createVisitRequest.getAppointmentDueDate(), appointmentReminderConfiguration));
            }
        }
        visit.addAppointment(createVisitRequest.getAppointmentDueDate(), appointmentReminders);
    }
}
