package org.motechproject.appointments.api.dao;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.appointments.api.model.Visit;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class AllVisitsTest {
    @Mock
    CouchDbConnector couchDbConnector;

    @InjectMocks
    AllVisits visitsDAO;

    @Before
    public void setUp() {
        couchDbConnector = mock(CouchDbConnector.class);
        visitsDAO = new AllVisits(couchDbConnector);
        MockitoAnnotations.initMocks(this);
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
}
