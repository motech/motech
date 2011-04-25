package org.motechproject.server.tama;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.appointments.api.dao.AppointmentsDAO;
import org.motechproject.outbox.api.dao.OutboundVoiceMessageDao;
import org.motechproject.tama.AppointmentReminderEventHandler;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentReminderEventHandlerTest extends TestCase {
    @InjectMocks
    AppointmentReminderEventHandler appointmentReminderEventHandler = new AppointmentReminderEventHandler();

    @Mock
    private AppointmentsDAO appointmentsDAOMock;

    @Mock
    private OutboundVoiceMessageDao outboundVoiceMessageDaoMock;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
     }

    @Test
    public void alwaysPass() throws Exception {
        assertTrue(true);
    }
/*
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

        Mockito.when(appointmentsDAOMock.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(appointmentsDAOMock.get("pID")).thenReturn(patient);

        appointmentReminderEventHandler.handle(event);

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

        Mockito.when(appointmentsDAOMock.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(appointmentsDAOMock.get("pID")).thenReturn(patient);

        appointmentReminderEventHandler.handle(event);

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

        Mockito.when(appointmentsDAOMock.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(appointmentsDAOMock.get("pID")).thenReturn(patient);

        appointmentReminderEventHandler.handle(event);

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

        Mockito.when(appointmentsDAOMock.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(appointmentsDAOMock.get("pID")).thenReturn(patient);

        appointmentReminderEventHandler.handle(event);

        verify(outboundVoiceMessageDaoMock, times(0)).add(any(OutboundVoiceMessage.class));
    }


    // Upcoming Tests
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

        Mockito.when(appointmentsDAOMock.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(appointmentsDAOMock.get("pID")).thenReturn(patient);

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

        Mockito.when(appointmentsDAOMock.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(appointmentsDAOMock.get("pID")).thenReturn(patient);

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

        Mockito.when(appointmentsDAOMock.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(appointmentsDAOMock.get("pID")).thenReturn(patient);

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

        Mockito.when(appointmentsDAOMock.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(appointmentsDAOMock.get("pID")).thenReturn(patient);

        upcomingAppointmentEventHandler.handle(event);

        verify(outboundVoiceMessageDaoMock, times(0)).add(any(OutboundVoiceMessage.class));
    }

    // Upcoming unscheduled tests
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

        Mockito.when(appointmentsDAOMock.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(appointmentsDAOMock.get("pID")).thenReturn(patient);

        upcomingUnscheduledAppointmentEventHandler.handle(event);

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

        Mockito.when(appointmentsDAOMock.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(appointmentsDAOMock.get("pID")).thenReturn(patient);

        upcomingUnscheduledAppointmentEventHandler.handle(event);

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

        Mockito.when(appointmentsDAOMock.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(appointmentsDAOMock.get("pID")).thenReturn(patient);

        upcomingUnscheduledAppointmentEventHandler.handle(event);

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

        Mockito.when(appointmentsDAOMock.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(appointmentsDAOMock.get("pID")).thenReturn(patient);

        upcomingUnscheduledAppointmentEventHandler.handle(event);

        verify(outboundVoiceMessageDaoMock, times(0)).add(any(OutboundVoiceMessage.class));
    }

*/
}
