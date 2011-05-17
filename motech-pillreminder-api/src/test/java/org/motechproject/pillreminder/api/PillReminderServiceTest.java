package org.motechproject.pillreminder.api;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
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
import org.motechproject.pillreminder.api.dao.PillReminderDao;
import org.motechproject.pillreminder.api.model.PillReminder;

public class PillReminderServiceTest
{
    @Mock
    EventRelay eventRelay;

    @Mock
    CouchDbConnector couchDbConnector;

    @Mock
    PillReminderDao pillReminderDao;

    @InjectMocks
    PillReminderService pillReminderService;

    @Before
    public void setUp() {
        
        pillReminderService = new PillReminderService();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddPillReminder() {
    	PillReminder a = new PillReminder();

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);

        pillReminderService.addPillReminder(a);

        verify(pillReminderDao).add(a);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.PILLREMINDER_CREATED_SUBJECT.equals(event.getSubject()));
    }

    @Test
    public void testUpdatePillReminder() {
    	PillReminder a = new PillReminder();

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);

        pillReminderService.updatePillReminder(a);

        verify(pillReminderDao).update(a);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.PILLREMINDER_UPDATED_SUBJECT.equals(event.getSubject()));
    }

    @Test
    public void testRemovePillReminder() {
    	PillReminder a = new PillReminder();

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);

        pillReminderService.removePillReminder(a);

        verify(pillReminderDao).remove(a);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.PILLREMINDER_DELETED_SUBJECT.equals(event.getSubject()));
    }

    @Test
    public void testRemovePillReminderById() {
    	PillReminder a = new PillReminder();
        a.setId("aID");

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);
        when(pillReminderDao.get("aID")).thenReturn(a);

        pillReminderService.removePillReminder(a.getId());

        verify(pillReminderDao).remove(a);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.PILLREMINDER_DELETED_SUBJECT.equals(event.getSubject()));
    }

    @Test
    public void testFindByExternalId() {
        List<PillReminder> list = pillReminderService.findByExternalId("eID");
        assertTrue(list.isEmpty());
    }
    
    @Test
    public void testFindByExternalIdAndWithinWindow() {
    	List<PillReminder> list = pillReminderService.getRemindersWithinWindow("eID", new Date());
    	assertTrue(list.isEmpty());
    }
    
}
