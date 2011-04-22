package org.motechproject.appointmentreminder;

import org.motechproject.model.MotechEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class EventKeys
{
	private final static Logger log = LoggerFactory.getLogger("org.motechproject.server.appointmentreminder");

    public final static String APPOINTMENT_ID_KEY = "AppointmentID";

    public final static String REMINDER_EVENT_SUBJECT = "org.motechproject.server.appointmentreminder.reminder";
    public final static String SCHEDULE_REMINDER_SUBJECT = "org.motechproject.server.appointmentreminder.schedule-reminder";
    public final static String UNSCHEDULE_REMINDER_SUBJECT = "org.motechproject.server.appointmentreminder.unschedule-reminder";

    public final static String UNSCHEDULED_APPOINTMENT_UPCOMING = "org.motechproject.server.appointmentreminder.unscheduled-appointment.upcoming";
    public final static String UNSCHEDULED_APPOINTMENT_MISSED = "org.motechproject.server.appointmentreminder.unscheduled-appointment.missed";
    public final static String SCHEDULED_APPOINTMENT_UPCOMING = "org.motechproject.server.appointmentreminder.scheduled-appointment.missed";
    public final static String SCHEDULED_APPOINTMENT_MISSED = "org.motechproject.server.appointmentreminder.scheduled-appointment.missed";

    public static String getAppointmentId(MotechEvent event)
    {
        String appointmentId = null;
        try {
            appointmentId = (String) event.getParameters().get(EventKeys.APPOINTMENT_ID_KEY);
        } catch (ClassCastException e) {
            log.warn("Event: " + event + " Key: " + EventKeys.APPOINTMENT_ID_KEY + " is not a String");
        }

        return appointmentId;
    }
}
