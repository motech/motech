package org.motechproject.appointments.api.dao;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.appointments.api.model.Appointment;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class AllAppointmentsTest {
    @Mock
    CouchDbConnector couchDbConnector;

    @InjectMocks
    AllAppointments allAppointments;

    @Before
    public void setUp() {
        couchDbConnector = mock(CouchDbConnector.class);
        allAppointments = new AllAppointments(couchDbConnector);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindByExternalId() {
        List<Appointment> list = allAppointments.findByExternalId("eID");

        assertTrue(list.isEmpty());
    }
}
