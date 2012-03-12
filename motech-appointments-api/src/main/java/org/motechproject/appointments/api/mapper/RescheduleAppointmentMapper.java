package org.motechproject.appointments.api.mapper;

import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.contract.RescheduleAppointmentRequest;
import org.motechproject.appointments.api.model.Reminder;

import java.util.ArrayList;
import java.util.List;

public class RescheduleAppointmentMapper {

    public List<Reminder> map(RescheduleAppointmentRequest rescheduleAppointmentRequest) {
        List<Reminder> appointmentReminders = new ArrayList<Reminder>();
        for (ReminderConfiguration appointmentReminderConfiguration : rescheduleAppointmentRequest.getAppointmentReminderConfigurations()) {
            appointmentReminders.add(new ReminderMapper().map(rescheduleAppointmentRequest.getAppointmentDueDate(), appointmentReminderConfiguration));
        }
        return appointmentReminders;
    }
}
