package org.motechproject.server.tama;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.appointmentreminder.EventKeys;
import org.motechproject.appointmentreminder.dao.PatientDAO;
import org.motechproject.appointmentreminder.model.Appointment;
import org.motechproject.appointmentreminder.model.Patient;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.service.ivr.CallRequest;
import org.motechproject.server.service.ivr.IVRService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UpcomingUnscheduledAppointmentEventHandlerTest extends TestCase {

    @InjectMocks
    UpcomingUnscheduledAppointmentEventHandler upcomingUnscheduledAppointmentEventHandler = new UpcomingUnscheduledAppointmentEventHandler();

    @Mock
    private PatientDAO patientDAOMock;

    @Mock
    private IVRService ivrServiceMock;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        upcomingUnscheduledAppointmentEventHandler.setVxmlUrl("http://test.org/");
     }

    @Test
    public void testHandle() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.APPOINTMENT_ID_KEY, "aID");

        MotechEvent event = new MotechEvent("", params);

        Appointment appointment = new Appointment();
        appointment.setPatientId("pID");
        Patient patient = new Patient();
        patient.setPhoneNumber("SIP/1000");

        Mockito.when(patientDAOMock.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(patientDAOMock.get("pID")).thenReturn(patient);

        upcomingUnscheduledAppointmentEventHandler.handle(event);

        verify(ivrServiceMock, times(1)).initiateCall(any(CallRequest.class));
    }

    @Test
    public void testHandle_NoIDInEvent() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();

        MotechEvent event = new MotechEvent("", params);

        Appointment appointment = new Appointment();
        appointment.setPatientId("pID");
        Patient patient = new Patient();
        patient.setPhoneNumber("SIP/1000");

        Mockito.when(patientDAOMock.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(patientDAOMock.get("pID")).thenReturn(patient);

        upcomingUnscheduledAppointmentEventHandler.handle(event);

        verify(ivrServiceMock, times(0)).initiateCall(any(CallRequest.class));
    }

    @Test
    public void testHandle_NullIDInEvent() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.APPOINTMENT_ID_KEY, null);

        MotechEvent event = new MotechEvent("", params);

        Appointment appointment = new Appointment();
        appointment.setPatientId("pID");
        Patient patient = new Patient();
        patient.setPhoneNumber("SIP/1000");

        Mockito.when(patientDAOMock.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(patientDAOMock.get("pID")).thenReturn(patient);

        upcomingUnscheduledAppointmentEventHandler.handle(event);

        verify(ivrServiceMock, times(0)).initiateCall(any(CallRequest.class));
    }

    @Test
    public void testHandle_InvalidIDInEvent() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.APPOINTMENT_ID_KEY, "invalid");

        MotechEvent event = new MotechEvent("", params);

        Appointment appointment = new Appointment();
        appointment.setPatientId("pID");
        Patient patient = new Patient();
        patient.setPhoneNumber("SIP/1000");

        Mockito.when(patientDAOMock.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(patientDAOMock.get("pID")).thenReturn(patient);

        upcomingUnscheduledAppointmentEventHandler.handle(event);

        verify(ivrServiceMock, times(0)).initiateCall(any(CallRequest.class));
    }
}
