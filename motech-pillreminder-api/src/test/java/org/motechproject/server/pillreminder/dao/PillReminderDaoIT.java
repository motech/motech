package org.motechproject.server.pillreminder.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.domain.PillReminder;
import org.motechproject.server.pillreminder.domain.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

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
	
	@Test
	public void testCRUD(){
		pillReminderDao.add(reminder);
		PillReminder pillReminder = pillReminderDao.get(reminder.getId());
		assertNotNull(pillReminder);
		pillReminder.setStartDate(new Date());
		pillReminderDao.update(pillReminder);
		pillReminderDao.remove(pillReminder);		
	}
}
