package org.motechproject.pillreminder.api;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.motechproject.pillreminder.api.dao.impl.PillReminderCouchDBDaoImpl;
import org.motechproject.pillreminder.api.model.PillReminder;

public class PillReminderServiceTest
{
    @Mock
    EventRelay eventRelay;

    @Mock
    CouchDbConnector couchDbConnector;

    @Mock
    PillReminderCouchDBDaoImpl pillReminderDAO;

    @InjectMocks
    PillReminderService pillReminderService;

    @Before
    public void setUp() {
        couchDbConnector = mock(CouchDbConnector.class);
        pillReminderDAO = new PillReminderCouchDBDaoImpl(couchDbConnector);

        pillReminderService = new PillReminderService();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddAppointment() {
    	PillReminder a = new PillReminder();

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);

        pillReminderService.addPillReminder(a);

        verify(pillReminderDAO).add(a);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.PILLREMINDER_CREATED_SUBJECT.equals(event.getSubject()));
    }

    @Test
    public void testUpdateAppointment() {
    	PillReminder a = new PillReminder();

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);

        pillReminderService.updatePillReminder(a);

        verify(pillReminderDAO).update(a);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.PILLREMINDER_UPDATED_SUBJECT.equals(event.getSubject()));
    }

    @Test
    public void testRemoveAppointment() {
    	PillReminder a = new PillReminder();

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);

        pillReminderService.removePillReminder(a);

        verify(pillReminderDAO).remove(a);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.PILLREMINDER_DELETED_SUBJECT.equals(event.getSubject()));
    }

    @Test
    public void testRemoveAppointmentById() {
    	PillReminder a = new PillReminder();
        a.setId("aID");

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);
        when(pillReminderDAO.get("aID")).thenReturn(a);

        pillReminderService.removePillReminder(a.getId());

        verify(pillReminderDAO).remove(a);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.PILLREMINDER_DELETED_SUBJECT.equals(event.getSubject()));
    }

    @Test
    public void testFindByExternalId() {
//        List<PillReminder> list = appointmentService.findByExternalId("eID");
//
//        assertTrue(list.isEmpty());
    }
}
