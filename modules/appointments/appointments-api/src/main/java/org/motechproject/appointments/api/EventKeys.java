package org.motechproject.appointments.api;

public final class EventKeys {
    private EventKeys() { }

    public static final String EXTERNAL_ID_KEY = "external.id";
    public static final String VISIT_NAME = "VisitName";
    public static final String BASE_SUBJECT = "org.motechproject.appointments.api.";
    public static final String APPOINTMENT_REMINDER_EVENT_SUBJECT = BASE_SUBJECT + "Appointment.Reminder";
    public static final String VISIT_REMINDER_EVENT_SUBJECT = BASE_SUBJECT + "Visit.Reminder";
}
