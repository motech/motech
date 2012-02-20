package org.motechproject.appointments.api.contract;

import java.util.List;

public class AppointmentCalendarRequest {

    private String externalId;

    private ReminderConfiguration reminderConfiguration;

    private List<Integer> weekOffsets;

    public String getExternalId() {
        return externalId;
    }

    public AppointmentCalendarRequest setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public ReminderConfiguration getReminderConfiguration() {
        return reminderConfiguration;
    }

    public AppointmentCalendarRequest setReminderConfiguration(ReminderConfiguration reminderConfiguration) {
        this.reminderConfiguration = reminderConfiguration;
        return this;
    }

    public List<Integer> getWeekOffsets() {
        return weekOffsets;
    }

    public AppointmentCalendarRequest setWeekOffsets(List<Integer> weekOffsets) {
        this.weekOffsets = weekOffsets;
        return this;
    }
}
