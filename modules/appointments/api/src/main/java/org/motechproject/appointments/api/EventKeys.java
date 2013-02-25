package org.motechproject.appointments.api;

public final class EventKeys {

    private EventKeys() { }

    public static final String EXTERNAL_ID_KEY = "external.id";
    public static final String VISIT_NAME = "VisitName";
    public static final String APPOINTMENT_ID = "AppointmentId";
    public static final String VISIT_DATE = "VisitDate";
    public static final String VISIT_REQUESTS = "VisitRequests";
    public static final String REMIND_FROM = "RemindFrom";
    public static final String INTERVAL_COUNT = "IntervalCount";
    public static final String INTERVAL_UNIT = "IntervalUnit";
    public static final String REPEAT_COUNT = "RepeatCount";

    public static final String BASE_SUBJECT = "org.motechproject.appointments.api.";
    public static final String APPOINTMENT_REMINDER_EVENT_SUBJECT = BASE_SUBJECT + "Appointment.Reminder";
    public static final String VISIT_REMINDER_EVENT_SUBJECT = BASE_SUBJECT + "Visit.Reminder";
    public static final String CREATE_APPOINTMENT_EVENT_SUBJECT = BASE_SUBJECT + "Create.Appointment";
    public static final String CREATED_APPOINTMENT_EVENT_SUBJECT = BASE_SUBJECT + "Created.Appointment";
    public static final String DELETED_APPOINTMENT_EVENT_SUBJECT = "Deleted.Appointments";
    public static final String MODIFY_APPOINTMENT_EVENT_SUBJECT = "Modify.Appointments";
    public static final String CREATED_VISIT_EVENT_SUBJECT = BASE_SUBJECT + "Created.Visit";
    public static final String CREATE_VISIT_EVENT_SUBJECT = BASE_SUBJECT + "Create.Visit";
    public static final String MISSED_VISIT_EVENT_SUBJECT = BASE_SUBJECT + "Missed.Visit";
}
