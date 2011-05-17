package org.motechproject.pillreminder.api.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.model.Time;
import org.motechproject.pillreminder.api.dao.PillReminderDao;
import org.motechproject.pillreminder.api.model.PillReminder;
import org.motechproject.pillreminder.api.model.Schedule;

@RunWith(MockitoJUnitRunner.class)
public class PillReminderDaoTest {

	@Mock
	CouchDbConnector db;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		PillReminder reminder = null;
		Schedule schedule = null;
		
		List<PillReminder> reminders = new ArrayList<PillReminder>();
		
		reminder = new PillReminder();
		reminder.setId("1");
		List<Schedule> schedules = new ArrayList<Schedule>();
		reminder.setStartDate(new DateTime(2011, 3, 1, 0, 0, 0, 0).toDate());
		reminder.setEndDate(new DateTime(2011, 3, 31, 0, 0, 0, 0).toDate());
		reminder.setSchedules(schedules);
		schedule = new Schedule();
		schedule.setWindowStart(new Time(7,0));
		schedule.setWindowEnd(new Time(9,0));
		schedules.add(schedule);
		
		schedule = new Schedule();
		schedule.setWindowStart(new Time(11,0));
		schedule.setWindowEnd(new Time(13,0));
		schedules.add(schedule);
		
		reminders.add(reminder);
		
		reminder = new PillReminder();
		reminder.setId("2");
		schedules = new ArrayList<Schedule>();
		reminder.setStartDate(new DateTime(2011, 4, 1, 0, 0, 0, 0).toDate());
		reminder.setEndDate(new DateTime(2011, 4, 30, 0, 0, 0, 0).toDate());
		reminder.setSchedules(schedules);
		schedule = new Schedule();
		schedule.setWindowStart(new Time(7,0));
		schedule.setWindowEnd(new Time(9,0));
		schedules.add(schedule);
		
		schedule = new Schedule();
		schedule.setWindowStart(new Time(11,0));
		schedule.setWindowEnd(new Time(13,0));
		schedules.add(schedule);
		
		reminders.add(reminder);
		
		when(db.queryView(any(ViewQuery.class), any(Class.class))).thenReturn(reminders);
		
	}
	
	@Test
	public void testFindByExternalIdAndWithinWindow() throws Exception {
		PillReminderDao dao = new PillReminderCouchDBDaoImpl(db);
		List<PillReminder> reminders = dao.findByExternalIdAndWithinWindow("patient-id-1", new DateTime(2011, 3, 1, 8, 0, 0, 0).toDate());
		assertEquals(1, reminders.size());
		
		DateTime start = new DateTime(2010, 5, 25, 12, 0, 0, 0);
		DateTime end = new DateTime(2010, 5, 25, 21, 0, 0, 0);
		Interval interval = new Interval(start, end);
		DateTime test = new DateTime(2010, 5, 25, 21, 0, 0, 0);
		System.out.println(interval.contains(test));

	}
}
