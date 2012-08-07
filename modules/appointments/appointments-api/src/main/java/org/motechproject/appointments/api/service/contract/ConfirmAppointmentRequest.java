package org.motechproject.appointments.api.service.contract;

import org.joda.time.DateTime;

/**
 * \ingroup Appointments
 *
 * Appointment schedule template for confirming appointments/scheduled visits.
 */

public class ConfirmAppointmentRequest {

    private String externalId;

    private String visitName;

    private DateTime appointmentConfirmDate;

    private ReminderConfiguration visitReminderConfiguration;

    public String getExternalId() {
        return externalId;
    }

    public ConfirmAppointmentRequest setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public String getVisitName() {
        return visitName;
    }

    public ConfirmAppointmentRequest setVisitName(String visitName) {
        this.visitName = visitName;
        return this;
    }

    public DateTime getAppointmentConfirmDate() {
        return appointmentConfirmDate;
    }

    public ConfirmAppointmentRequest setAppointmentConfirmDate(DateTime appointmentConfirmDate) {
        this.appointmentConfirmDate = appointmentConfirmDate;
        return this;
    }

    public ReminderConfiguration getVisitReminderConfiguration() {
        return visitReminderConfiguration;
    }

    public ConfirmAppointmentRequest setVisitReminderConfiguration(ReminderConfiguration visitReminderConfiguration) {
        this.visitReminderConfiguration = visitReminderConfiguration;
        return this;
    }
}
