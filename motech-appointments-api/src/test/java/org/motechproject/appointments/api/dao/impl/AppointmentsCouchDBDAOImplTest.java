package org.motechproject.appointments.api.dao.impl;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.eventgateway.EventGateway;
import org.motechproject.model.MotechEvent;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AppointmentsCouchDBDAOImplTest
{
    @Mock
    EventGateway eventGatewayMock;

    @Mock
    CouchDbConnector couchDbConnector;

    @InjectMocks
    AppointmentsCouchDBDAOImpl appointmentsDAO;

    @Before
    public void setUp() {
        couchDbConnector = mock(CouchDbConnector.class);
        appointmentsDAO = new AppointmentsCouchDBDAOImpl(couchDbConnector);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddAppointment() {
        Appointment a = new Appointment();

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);

        appointmentsDAO.addAppointment(a);

        verify(couchDbConnector).create(a);
        verify(eventGatewayMock).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.APPOINTMENT_CREATED_SUBJECT.equals(event.getSubject()));
    }



/*
                public void addAppointment(Appointment appointment);
    public void updateAppointment(Appointment appointment);
    public Appointment getAppointment(String appointmentId);
    public void removeAppointment(String appointmentId);
    public void removeAppointment(Appointment appointment);
             */
}
