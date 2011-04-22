package org.motechproject.server.tama;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.appointmentreminder.EventKeys;
import org.motechproject.appointmentreminder.dao.PatientDAO;
import org.motechproject.appointmentreminder.model.Appointment;
import org.motechproject.appointmentreminder.model.Patient;
import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.dao.OutboundVoiceMessageDao;
import org.motechproject.outbox.model.OutboundVoiceMessage;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UpcomingAppointmentEventHandlerTest extends TestCase {

    @InjectMocks
    UpcomingAppointmentEventHandler upcomingAppointmentEventHandler = new UpcomingAppointmentEventHandler();

    @Mock
    private PatientDAO patientDAOMock;

    @Mock
    private OutboundVoiceMessageDao outboundVoiceMessageDaoMock;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        upcomingAppointmentEventHandler.setVxmlUrl("http://test.org/");
     }

    @Test
    public void testHandle() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.APPOINTMENT_ID_KEY, "aID");

        MotechEvent event = new MotechEvent("", params);

        Appointment appointment = new Appointment();
        appointment.setPatientId("pID");
        Patient patient = new Patient();
        patient.setId("pID");
        patient.setPhoneNumber("SIP/1000");

        ArgumentCaptor<OutboundVoiceMessage> argument = ArgumentCaptor.forClass(OutboundVoiceMessage.class);

        Mockito.when(patientDAOMock.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(patientDAOMock.get("pID")).thenReturn(patient);

        upcomingAppointmentEventHandler.handle(event);

        verify(outboundVoiceMessageDaoMock, times(1)).add(argument.capture());

        OutboundVoiceMessage msg = argument.getValue();
        assertEquals("http://test.org/?aptId=aID", msg.getVoiceMessageType().getvXmlUrl());
        assertEquals("pID", msg.getPartyId());
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

        upcomingAppointmentEventHandler.handle(event);

        verify(outboundVoiceMessageDaoMock, times(0)).add(any(OutboundVoiceMessage.class));
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

        upcomingAppointmentEventHandler.handle(event);

        verify(outboundVoiceMessageDaoMock, times(0)).add(any(OutboundVoiceMessage.class));
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

        upcomingAppointmentEventHandler.handle(event);

        verify(outboundVoiceMessageDaoMock, times(0)).add(any(OutboundVoiceMessage.class));
    }
}
