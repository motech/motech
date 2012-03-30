package org.motechproject.appointments.api.service.contract;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class RescheduleAppointmentRequest {

    private String externalId;

    private String visitName;

    private DateTime appointmentDueDate;

    private List<ReminderConfiguration> appointmentReminderConfigurations = new ArrayList<ReminderConfiguration>();

    public String getExternalId() {
        return externalId;
    }

    public RescheduleAppointmentRequest setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public String getVisitName() {
        return visitName;
    }

    public RescheduleAppointmentRequest setVisitName(String visitName) {
        this.visitName = visitName;
        return this;
    }

    public DateTime getAppointmentDueDate() {
        return appointmentDueDate;
    }

    public RescheduleAppointmentRequest setAppointmentDueDate(DateTime appointmentDueDate) {
        this.appointmentDueDate = appointmentDueDate;
        return this;
    }

    public List<ReminderConfiguration> getAppointmentReminderConfigurations() {
        return appointmentReminderConfigurations;
    }

    public RescheduleAppointmentRequest addAppointmentReminderConfiguration(ReminderConfiguration appointmentReminderConfiguration) {
        this.appointmentReminderConfigurations.add(appointmentReminderConfiguration);
        return this;
    }
}
