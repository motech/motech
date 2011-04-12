package org.motechproject.server.appointmentreminder;

import org.motechproject.model.MotechEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 *
 */
public class EventKeys
{
	private final static Logger log = LoggerFactory.getLogger("org.motechproject.server.appointmentreminder");

    public final static String APPOINTMENT_ID_KEY = "AppointmentID";
    public final static String CALL_DATE_KEY = "CallDate";
    public final static String PATIENT_ID_KEY = "PatientID";

    public final static String REMINDER_EVENT_SUBJECT = "org.motechproject.server.appointmentreminder.reminder-call";
    public final static String SCHEDULE_REMINDER_SUBJECT = "org.motechproject.server.appointmentreminder.schedule-reminder-call";
    public final static String UNSCHEDULE_REMINDER_SUBJECT = "org.motechproject.server.appointmentreminder.unschedule-reminder-call";
    public final static String INCOMPLETE_CALL_SUBJECT = "org.motechproject.server.appointmentreminder.incomplete-reminder-call";
    public final static String COMPLETED_CALL_SUBJECT = "org.motechproject.server.appointmentreminder.completed-reminder-call";

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

    public static String getPatientId(MotechEvent event)
    {
        String patientId = null;
        try {
            patientId = (String) event.getParameters().get(EventKeys.PATIENT_ID_KEY);
        } catch (ClassCastException e) {
            log.warn("Event: " + event + " Key: " + EventKeys.PATIENT_ID_KEY + " is not a String");
        }

        return patientId;
    }

    public static Date getCallDate(MotechEvent event)
    {
        Date callDate = null;
        try {
            callDate = (Date) event.getParameters().get(EventKeys.CALL_DATE_KEY);
        } catch (ClassCastException e) {
            log.warn("Event: " + event + " Key: " + EventKeys.CALL_DATE_KEY + " is not a Date");
        }

        return callDate;
    }
}
