package org.motechproject.appointments.api.dao.impl;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class VisitsCouchDBDAOImplTest
{
    @Mock
    EventRelay eventRelay;

    @Mock
    CouchDbConnector couchDbConnector;

    @InjectMocks
    VisitsCouchDBDAOImpl visitsDAO;

    @Before
    public void setUp() {
        couchDbConnector = mock(CouchDbConnector.class);
        visitsDAO = new VisitsCouchDBDAOImpl(couchDbConnector);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddVisit() {
        Visit v = new Visit();
        v.setAppointmentId("aID");

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);

        visitsDAO.addVisit(v);

        verify(couchDbConnector).create(v);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.VISIT_CREATED_SUBJECT.equals(event.getSubject()));
    }

    @Test
    public void testUpdateVisit() {
        Visit v = new Visit();
        v.setAppointmentId("aID");

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);

        visitsDAO.updateVisit(v);

        verify(couchDbConnector).update(v);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.VISIT_UPDATED_SUBJECT.equals(event.getSubject()));
    }

    @Test
    public void testRemoveVisit() {
        Visit v = new Visit();

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);

        visitsDAO.removeVisit(v);

        verify(couchDbConnector).delete(v);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.VISIT_DELETED_SUBJECT.equals(event.getSubject()));
    }

    @Test
    public void testRemoveVisitById() {
        Visit v = new Visit();
        v.setId("rID");

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);
        when(visitsDAO.getVisit("rID")).thenReturn(v);

        visitsDAO.removeVisit(v.getId());

        verify(couchDbConnector).delete(v);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.VISIT_DELETED_SUBJECT.equals(event.getSubject()));
    }

    @Test
    public void testFindByExternalId() {
        List<Visit> list = visitsDAO.findByExternalId("eID");

        assertTrue(list.isEmpty());
    }

    @Test
    public void testFindByAppointmentId() {
        List<Visit> list = visitsDAO.findByAppointmentId("aID");

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
