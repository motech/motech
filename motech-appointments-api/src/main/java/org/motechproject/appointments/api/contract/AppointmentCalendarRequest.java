package org.motechproject.appointments.api.contract;

import java.util.List;

public class AppointmentCalendarRequest {

    private String externalId;

    private ReminderConfiguration appointmentReminderConfiguration;

    private ReminderConfiguration visitReminderConfiguration;

    private List<Integer> weekOffsets;

    public String getExternalId() {
        return externalId;
    }

    public AppointmentCalendarRequest setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public ReminderConfiguration getAppointmentReminderConfiguration() {
        return appointmentReminderConfiguration;
    }

    public AppointmentCalendarRequest setAppointmentReminderConfiguration(ReminderConfiguration appointmentReminderConfiguration) {
        this.appointmentReminderConfiguration = appointmentReminderConfiguration;
        return this;
    }

    public List<Integer> getWeekOffsets() {
        return weekOffsets;
    }

    public AppointmentCalendarRequest setWeekOffsets(List<Integer> weekOffsets) {
        this.weekOffsets = weekOffsets;
        return this;
    }

    public ReminderConfiguration getVisitReminderConfiguration() {
        return visitReminderConfiguration;
    }

    public AppointmentCalendarRequest setVisitReminderConfiguration(ReminderConfiguration visitReminderConfiguration) {
        this.visitReminderConfiguration = visitReminderConfiguration;
        return this;
    }
}
