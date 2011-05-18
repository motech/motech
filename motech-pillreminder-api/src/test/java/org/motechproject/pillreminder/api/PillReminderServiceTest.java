package org.motechproject.pillreminder.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.pillreminder.api.dao.PillReminderDao;
import org.motechproject.pillreminder.api.model.Medicine;
import org.motechproject.pillreminder.api.model.PillReminder;
import org.motechproject.pillreminder.api.model.Schedule;
import org.motechproject.pillreminder.api.model.Status;

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
    public void testGetRemindersWithinWindow() {
    	List<PillReminder> list = pillReminderService.getRemindersWithinWindow("eID", new Date());
    	assertTrue(list.isEmpty());
    }
    
    @Test
    public void testGetResult(){
    	boolean result = pillReminderService.getResult("eID", "med", new Date());
    	assertFalse(result);
    	
		List<PillReminder> reminders = new ArrayList<PillReminder>();
		PillReminder reminder = new PillReminder();
		Medicine medicine = new Medicine();
		medicine.setName("a");
		Status status = new Status();
		Date date = new DateTime(2011, 5, 17, 0, 0, 0, 0).toDate();
		status.setDate(date);
		status.setWindowStartTime(new Time(9, 0));
		status.setTaken(true);
		medicine.getStatuses().add(status);
		
		status = new Status();
		status.setDate(date);
		status.setWindowStartTime(new Time(17, 0));
		status.setTaken(false);
		medicine.getStatuses().add(status);
		
		reminder.getMedicines().add(medicine);
		reminders.add(reminder);
		
		when(pillReminderDao.findByExternalIdAndWithinWindow(any(String.class), any(Date.class))).thenReturn(reminders);
		
		result = pillReminderService.getResult("eID", "a", new DateTime(2011, 5, 17, 9, 0, 0, 0).toDate());
		assertTrue(result);
		
		result = pillReminderService.getResult("eID", "a", new DateTime(2011, 5, 17, 17, 0, 0, 0).toDate());
		assertFalse(result);
		
		result = pillReminderService.getResult("eID", "a", new DateTime(2011, 5, 17, 21, 0, 0, 0).toDate());
		assertFalse(result);
		
		result = pillReminderService.getResult("eID", "a", new DateTime(2011, 5, 18, 9, 0, 0, 0).toDate());
		assertFalse(result);
    }
    
    @Test
    public void testGetMedicinesWithinWindow(){
		PillReminder reminder = new PillReminder();
		Schedule schedule = new Schedule();
		reminder.getSchedules().add(schedule);
		schedule.setWindowStart(new Time(9,0));
		schedule.setWindowEnd(new Time(11,0));

		schedule = new Schedule();
		reminder.getSchedules().add(schedule);
		schedule.setWindowStart(new Time(17,0));
		schedule.setWindowEnd(new Time(19,0));

		Medicine medicine = new Medicine();
		reminder.getMedicines().add(medicine);
		medicine.setName("a");
		
		Status status = new Status();
		medicine.getStatuses().add(status);
		Date date = new DateTime(2011, 5, 17, 0, 0, 0, 0).toDate();
		status.setDate(date);
		status.setWindowStartTime(new Time(9, 0));
		status.setTaken(true);
		
		status = new Status();
		medicine.getStatuses().add(status);
		date = new DateTime(2011, 5, 17, 0, 0, 0, 0).toDate();
		status.setDate(date);
		status.setWindowStartTime(new Time(17, 0));
		status.setTaken(true);
		
		medicine= new Medicine();
		reminder.getMedicines().add(medicine);
		medicine.setName("b");

		status = new Status();
		medicine.getStatuses().add(status);
		date = new DateTime(2011, 5, 17, 0, 0, 0, 0).toDate();
		status.setDate(date);
		status.setWindowStartTime(new Time(9, 0));
		status.setTaken(true);
		
		status = new Status();
		medicine.getStatuses().add(status);
		date = new DateTime(2011, 5, 17, 0, 0, 0, 0).toDate();
		status.setDate(date);
		status.setWindowStartTime(new Time(17, 0));
		status.setTaken(false);
		
		when(pillReminderDao.get(any(String.class))).thenReturn(reminder);
		
		boolean result;
		List<String> meds = null;
		date = new DateTime(2011, 5, 16, 9, 0, 0, 0).toDate();
		result = pillReminderService.isPillReminderCompleted("rID", date);
		assertTrue(result);

		meds = pillReminderService.getMedicinesWithinWindow("rID", date);
		assertEquals(0, meds.size());
		
		date = new DateTime(2011, 5, 17, 9, 0, 0, 0).toDate();
		result = pillReminderService.isPillReminderCompleted("rID", date);
		assertTrue(result);
		
		meds = pillReminderService.getMedicinesWithinWindow("rID", date);
		assertEquals(0, meds.size());
		
		date = new DateTime(2011, 5, 17, 10, 10, 0, 0).toDate();
		result = pillReminderService.isPillReminderCompleted("rID", date);
		assertTrue(result);
		
		meds = pillReminderService.getMedicinesWithinWindow("rID", date);
		assertEquals(0, meds.size());
		
		date = new DateTime(2011, 5, 17, 17, 0, 0, 0).toDate();
		result = pillReminderService.isPillReminderCompleted("rID", date);
		assertFalse(result);
		
		meds = pillReminderService.getMedicinesWithinWindow("rID", date);
		assertEquals(1, meds.size());
		
		date = new DateTime(2011, 5, 17, 18, 0, 0, 0).toDate();
		result = pillReminderService.isPillReminderCompleted("rID", date);
		assertFalse(result);
		
		meds = pillReminderService.getMedicinesWithinWindow("rID", date);
		assertEquals(1, meds.size());
		
    }
}
