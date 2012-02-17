package org.motechproject.appointments.api;

public class EventKeys {
    public final static String APPOINTMENT_ID_KEY = "AppointmentID";
    public final static String REMINDER_ID_KEY = "ReminderID";
    public static final String EXTERNAL_ID_KEY = "external.id";
    public final static String JOB_ID_KEY = "JobID";

    public final static String BASE_SUBJECT = "org.motechproject.server.appointments.";
    public final static String APPOINTMENT_REMINDER_EVENT_SUBJECT = BASE_SUBJECT + "reminder";
    public static final String APPOINTMENT_REMINDER_EVENT_PREFIX = "org.motechproject.appointment.api.Reminder";
}
