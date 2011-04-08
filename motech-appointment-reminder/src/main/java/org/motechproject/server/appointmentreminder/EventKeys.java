package org.motechproject.server.appointmentreminder;

import org.motechproject.model.MotechEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: 4/7/11
 * Time: 9:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class EventKeys
{
	private final static Logger log = LoggerFactory.getLogger("org.motechproject.server.appointmentreminder");

    public final static String APPOINTMENT_ID_KEY = "AppointmentID";
    public final static String CALL_DATE_KEY = "CallDate";
    public final static String PATIENT_ID_KEY = "PatientID";

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
