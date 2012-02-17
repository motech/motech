package org.motechproject.appointments.api.dao;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.appointments.api.model.Reminder;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class AllRemindersTest {
    @Mock
    CouchDbConnector couchDbConnector;

    @InjectMocks
    AllReminders remindersDAO;

    @Before
    public void setUp() {
        couchDbConnector = mock(CouchDbConnector.class);
        remindersDAO = new AllReminders(couchDbConnector);
        MockitoAnnotations.initMocks(this);
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
}
