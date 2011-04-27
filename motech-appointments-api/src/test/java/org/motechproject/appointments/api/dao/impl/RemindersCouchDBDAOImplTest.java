package org.motechproject.appointments.api.dao.impl;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class RemindersCouchDBDAOImplTest
{
    @Mock
    EventRelay eventRelay;

    @Mock
    CouchDbConnector couchDbConnector;

    @InjectMocks
    RemindersCouchDBDAOImpl remindersDAO;

    @Before
    public void setUp() {
        couchDbConnector = mock(CouchDbConnector.class);
        remindersDAO = new RemindersCouchDBDAOImpl(couchDbConnector);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddReminder() {
        Reminder r = new Reminder();
        r.setAppointmentId("aID");

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);

        remindersDAO.addReminder(r);

        verify(couchDbConnector).create(r);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.REMINDER_CREATED_SUBJECT.equals(event.getSubject()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddReminder_NoAptId() {
        Reminder r = new Reminder();

        remindersDAO.addReminder(r);
    }

    @Test
    public void testUpdateReminder() {
        Reminder r = new Reminder();
        r.setAppointmentId("aID");

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);

        remindersDAO.updateReminder(r);

        verify(couchDbConnector).update(r);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.REMINDER_UPDATED_SUBJECT.equals(event.getSubject()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateReminder_NoAptId() {
        Reminder r = new Reminder();

        remindersDAO.updateReminder(r);
    }

    @Test
    public void testRemoveReminder() {
        Reminder r = new Reminder();

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);

        remindersDAO.removeReminder(r);

        verify(couchDbConnector).delete(r);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.REMINDER_DELETED_SUBJECT.equals(event.getSubject()));
    }

    @Test
    public void testRemoveReminderById() {
        Reminder r = new Reminder();
        r.setId("rID");

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);
        when(remindersDAO.getReminder("rID")).thenReturn(r);

        remindersDAO.removeReminder(r.getId());

        verify(couchDbConnector).delete(r);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.REMINDER_DELETED_SUBJECT.equals(event.getSubject()));
    }

    @Test
    public void testFindByExternalId() {
        List<Reminder> list = remindersDAO.findByExternalId("eID");

        assertTrue(list.isEmpty());
    }

    @Test
    public void testFindByAppointmentId() {
        List<Reminder> list = remindersDAO.findByAppointmentId("aID");

        assertTrue(list.isEmpty());
    }

/*
                public void addAppointment(Appointment appointment);
    public void updateAppointment(Appointment appointment);
    public Appointment getAppointment(String appointmentId);
    public void removeAppointment(String appointmentId);
    public void removeAppointment(Appointment appointment);
             */
}
