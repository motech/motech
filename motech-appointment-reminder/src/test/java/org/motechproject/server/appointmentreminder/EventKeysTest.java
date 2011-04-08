package org.motechproject.server.appointmentreminder;

import org.junit.Test;
import org.motechproject.model.MotechEvent;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 */
public class EventKeysTest
{

    @Test
    public void testGetAppointmentId_ValidId() throws Exception {

        String appointmentId = "1a";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.APPOINTMENT_ID_KEY, appointmentId);

        MotechEvent motechEvent = new MotechEvent("", "", params);

        String _aptId = EventKeys.getAppointmentId(motechEvent);

        assertEquals(appointmentId, _aptId);
    }

    @Test
    public void testGetAppointmentId_NoId() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();

        MotechEvent motechEvent = new MotechEvent("", "", params);

        String _aptId = EventKeys.getAppointmentId(motechEvent);

        assertNull(_aptId);
    }

    @Test
    public void testGetAppointmentId_InvalidId() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.APPOINTMENT_ID_KEY, new Integer(0));

        MotechEvent motechEvent = new MotechEvent("", "", params);

        String _aptId = EventKeys.getAppointmentId(motechEvent);

        assertNull(_aptId);
    }

    @Test
    public void testGetPatientId_ValidId() throws Exception {

        String patientId = "1a";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.PATIENT_ID_KEY, patientId);

        MotechEvent motechEvent = new MotechEvent("", "", params);

        String _Id = EventKeys.getPatientId(motechEvent);

        assertEquals(patientId, _Id);
    }

    @Test
    public void testGetPatientId_NoId() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();

        MotechEvent motechEvent = new MotechEvent("", "", params);

        String _Id = EventKeys.getPatientId(motechEvent);

        assertNull(_Id);
    }

    @Test
    public void testGetPatientId_InvalidId() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.PATIENT_ID_KEY, new Integer(0));

        MotechEvent motechEvent = new MotechEvent("", "", params);

        String _Id = EventKeys.getPatientId(motechEvent);

        assertNull(_Id);
    }

    @Test
    public void testGetCallDate_ValidId() throws Exception {

        Date callDate = new Date();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.CALL_DATE_KEY, callDate);

        MotechEvent motechEvent = new MotechEvent("", "", params);

        Date _date = EventKeys.getCallDate(motechEvent);

        assertEquals(callDate, _date);
    }

    @Test
    public void testGetCallDate_NoId() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();

        MotechEvent motechEvent = new MotechEvent("", "", params);

        Date _date = EventKeys.getCallDate(motechEvent);

        assertNull(_date);
    }

    @Test
    public void testGetCallDate_InvalidId() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.CALL_DATE_KEY, new Integer(0));

        MotechEvent motechEvent = new MotechEvent("", "", params);

        Date _date = EventKeys.getCallDate(motechEvent);

        assertNull(_date);
    }
}
