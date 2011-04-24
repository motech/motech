package org.motechproject.appointments.api;

import org.motechproject.model.MotechEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class EventKeys
{
	private final static Logger log = LoggerFactory.getLogger("org.motechproject.server.appointments");

    public final static String APPOINTMENT_ID_KEY = "AppointmentID";
    public final static String REMINDER_ID_KEY = "ReminderID";
    public final static String JOB_ID_KEY = "JobID";

    public final static String BASE_SUBJECT = "org.motechproject.server.appointments.";
    public final static String APPOINTMENT_CREATED_SUBJECT = BASE_SUBJECT + "appointment.created";
    public final static String APPOINTMENT_UPDATED_SUBJECT = BASE_SUBJECT + "appointment.updated";
    public final static String APPOINTMENT_DELETED_SUBJECT = BASE_SUBJECT + "appointment.deleted";

    public final static String APPOINTMENT_WINDOW_CREATED_SUBJECT = BASE_SUBJECT + "appointment-window.created";
    public final static String APPOINTMENT_WINDOW_UPDATED_SUBJECT = BASE_SUBJECT + "appointment-window.updated";
    public final static String APPOINTMENT_WINDOW_DELETED_SUBJECT = BASE_SUBJECT + "appointment-window.deleted";

    public final static String REMINDER_WILDCARD_SUBJECT = BASE_SUBJECT + "reminder.*";
    public final static String REMINDER_CREATED_SUBJECT = BASE_SUBJECT + "reminder.created";
    public final static String REMINDER_UPDATED_SUBJECT = BASE_SUBJECT + "reminder.updated";
    public final static String REMINDER_DELETED_SUBJECT = BASE_SUBJECT + "reminder.deleted";

    public final static String REMINDER_EVENT_SUBJECT = "org.motechproject.server.appointments.reminder";
    public final static String SCHEDULE_REMINDER_SUBJECT = "org.motechproject.server.appointments.schedule-reminder";
    public final static String UNSCHEDULE_REMINDER_SUBJECT = "org.motechproject.server.appointments.unschedule-reminder";

    public final static String UNSCHEDULED_APPOINTMENT_UPCOMING = "org.motechproject.server.appointments.unscheduled-appointment.upcoming";
    public final static String UNSCHEDULED_APPOINTMENT_MISSED = "org.motechproject.server.appointments.unscheduled-appointment.missed";
    public final static String SCHEDULED_APPOINTMENT_UPCOMING = "org.motechproject.server.appointments.scheduled-appointment.missed";
    public final static String SCHEDULED_APPOINTMENT_MISSED = "org.motechproject.server.appointments.scheduled-appointment.missed";

    public static String getAppointmentId(MotechEvent event)
    {
        return getStringValue(event, EventKeys.APPOINTMENT_ID_KEY);
    }

    public static String getJobId(MotechEvent event)
    {
        return getStringValue(event, EventKeys.JOB_ID_KEY);
    }

    public static String getReminderId(MotechEvent event)
    {
        return getStringValue(event, EventKeys.REMINDER_ID_KEY);
    }

    public static String getStringValue(MotechEvent event, String key)
    {
        String ret = null;
        try {
            ret = (String) event.getParameters().get(key);
        } catch (ClassCastException e) {
            log.warn("Event: " + event + " Key: " + key + " is not a String");
        }

        return ret;
    }
}
