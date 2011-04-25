package org.motechproject.server.tama;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.dao.AppointmentsDAO;
import org.motechproject.appointments.api.dao.RemindersDAO;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.metrics.MetricsAgent;
import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.dao.OutboundVoiceMessageDao;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.tama.AppointmentReminderEventHandler;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentReminderEventHandlerTest extends TestCase {
    @InjectMocks
    AppointmentReminderEventHandler appointmentReminderEventHandler = new AppointmentReminderEventHandler();

    @Mock
    private AppointmentsDAO appointmentsDAO;

    @Mock
    private RemindersDAO remindersDAO;

    @Mock
    private OutboundVoiceMessageDao outboundVoiceMessageDaoMock;

    @Mock
    private MetricsAgent metricsAgent;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
     }

    @Test
    public void testHandle_NoAptId() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();

        MotechEvent event = new MotechEvent("", params);

        appointmentReminderEventHandler.handle(event);

        verify(appointmentsDAO, times(0)).getAppointment(anyString());
        verify(outboundVoiceMessageDaoMock, times(0)).add(any(OutboundVoiceMessage.class));
    }

    @Test
    public void testHandle_NullIDInEvent() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.APPOINTMENT_ID_KEY, null);

        MotechEvent event = new MotechEvent("", params);

        appointmentReminderEventHandler.handle(event);

        verify(appointmentsDAO, times(0)).getAppointment(anyString());
        verify(outboundVoiceMessageDaoMock, times(0)).add(any(OutboundVoiceMessage.class));
    }

    @Test
    public void testHandle_NullApt() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.APPOINTMENT_ID_KEY, "aID");

        MotechEvent event = new MotechEvent("", params);

        Mockito.when(appointmentsDAO.getAppointment("aID")).thenReturn(null);

        appointmentReminderEventHandler.handle(event);

        verify(outboundVoiceMessageDaoMock, times(0)).add(Matchers.<OutboundVoiceMessage>anyObject());
    }

    @Test
    public void testHandle_NeedToSchedule() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.APPOINTMENT_ID_KEY, "aID");

        MotechEvent event = new MotechEvent("", params);

        Appointment appointment = new Appointment();
        appointment.setId("aID");
        appointment.setExternalId("pID");
        appointment.setDueDate(new Date());
        appointment.setScheduledDate(null);

        Mockito.when(appointmentsDAO.getAppointment("aID")).thenReturn(appointment);

        ArgumentCaptor<OutboundVoiceMessage> argument = ArgumentCaptor.forClass(OutboundVoiceMessage.class);
        appointmentReminderEventHandler.handle(event);

        verify(outboundVoiceMessageDaoMock, times(1)).add(argument.capture());

        OutboundVoiceMessage msg = argument.getValue();
        assertTrue(msg.getVoiceMessageType().getvXmlUrl().endsWith("schedule"));
    }

    @Test
    public void testHandle_ScheduledAndVisited() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.APPOINTMENT_ID_KEY, "aID");
        params.put(EventKeys.REMINDER_ID_KEY, "rID");

        MotechEvent event = new MotechEvent("", params);

        Appointment appointment = new Appointment();
        appointment.setId("aID");
        appointment.setExternalId("pID");
        appointment.setDueDate(new Date());
        appointment.setScheduledDate(new Date());

        Visit v = new Visit();
        appointment.setVisit(v);

        Reminder reminder = new Reminder();

        Mockito.when(appointmentsDAO.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(remindersDAO.getReminder("rID")).thenReturn(reminder);

        appointmentReminderEventHandler.handle(event);

        verify(outboundVoiceMessageDaoMock, times(0)).add(any(OutboundVoiceMessage.class));
    }

    @Test
    public void testHandle_ScheduledAndVisitedNoReminderId() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.APPOINTMENT_ID_KEY, "aID");

        MotechEvent event = new MotechEvent("", params);

        Appointment appointment = new Appointment();
        appointment.setId("aID");
        appointment.setExternalId("pID");
        appointment.setDueDate(new Date());
        appointment.setScheduledDate(new Date());

        Visit v = new Visit();
        appointment.setVisit(v);

        Mockito.when(appointmentsDAO.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(remindersDAO.getReminder("rID")).thenReturn(null);

        appointmentReminderEventHandler.handle(event);

        verify(outboundVoiceMessageDaoMock, times(0)).add(any(OutboundVoiceMessage.class));
    }

    @Test
    public void testHandle_ScheduledAndVisitedNullReminder() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.APPOINTMENT_ID_KEY, "aID");
        params.put(EventKeys.REMINDER_ID_KEY, "rID");

        MotechEvent event = new MotechEvent("", params);

        Appointment appointment = new Appointment();
        appointment.setId("aID");
        appointment.setExternalId("pID");
        appointment.setDueDate(new Date());
        appointment.setScheduledDate(new Date());

        Visit v = new Visit();
        appointment.setVisit(v);

        Mockito.when(appointmentsDAO.getAppointment("aID")).thenReturn(appointment);
        Mockito.when(remindersDAO.getReminder("rID")).thenReturn(null);

        appointmentReminderEventHandler.handle(event);

        verify(outboundVoiceMessageDaoMock, times(0)).add(any(OutboundVoiceMessage.class));
    }

    @Test
    public void testHandle_ScheduledAndNotVisitedUpcoming() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.APPOINTMENT_ID_KEY, "aID");

        MotechEvent event = new MotechEvent("", params);

        Appointment appointment = new Appointment();
        appointment.setId("aID");
        appointment.setExternalId("pID");
        appointment.setDueDate(new Date());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        appointment.setScheduledDate(cal.getTime());

        Mockito.when(appointmentsDAO.getAppointment("aID")).thenReturn(appointment);
        ArgumentCaptor<OutboundVoiceMessage> argument = ArgumentCaptor.forClass(OutboundVoiceMessage.class);

        appointmentReminderEventHandler.handle(event);

        verify(outboundVoiceMessageDaoMock, times(1)).add(argument.capture());

        OutboundVoiceMessage msg = argument.getValue();
        assertTrue(msg.getVoiceMessageType().getvXmlUrl().endsWith("upcoming"));
    }

    @Test
    public void testHandle_ScheduledAndNotVisitedMissed() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.APPOINTMENT_ID_KEY, "aID");

        MotechEvent event = new MotechEvent("", params);

        Appointment appointment = new Appointment();
        appointment.setId("aID");
        appointment.setExternalId("pID");
        appointment.setDueDate(new Date());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -11);
        appointment.setScheduledDate(cal.getTime());

        Mockito.when(appointmentsDAO.getAppointment("aID")).thenReturn(appointment);
        ArgumentCaptor<OutboundVoiceMessage> argument = ArgumentCaptor.forClass(OutboundVoiceMessage.class);

        appointmentReminderEventHandler.handle(event);

        verify(outboundVoiceMessageDaoMock, times(1)).add(argument.capture());

        OutboundVoiceMessage msg = argument.getValue();
        assertTrue(msg.getVoiceMessageType().getvXmlUrl().endsWith("missed"));
    }
}
