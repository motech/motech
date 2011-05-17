package org.motechproject.pillreminder.api.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.pillreminder.api.model.PillReminder;
import org.motechproject.pillreminder.api.model.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/testPillReminder.xml"})
public class PillReminderDaoIT {

	@Autowired
	private PillReminderDao pillReminderDao;
	
	private PillReminder reminder;
	
	@Before
	public void setUp() {
		Schedule schedule = new Schedule();
		schedule.setWindowStart(new Time(9, 0));
		schedule.setWindowEnd(new Time(11,0));
		reminder = new PillReminder();
		reminder.setStartDate(new Date());
		reminder.getSchedules().add(schedule);
	}
	
	@After
	public void tearDown() {
		for(PillReminder reminder : pillReminderDao.getAll()) {
			pillReminderDao.remove(reminder);
		}
	}
	
	@Test
	public void testAdd(){
		pillReminderDao.add(reminder);
		
		PillReminder pillReminder = pillReminderDao.get(reminder.getId());
		
	}
}
