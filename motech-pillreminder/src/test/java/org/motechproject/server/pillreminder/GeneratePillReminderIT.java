package org.motechproject.server.pillreminder;

import static junit.framework.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.pillreminder.api.PillReminderService;
import org.motechproject.pillreminder.api.model.Medicine;
import org.motechproject.pillreminder.api.model.PillReminder;
import org.motechproject.pillreminder.api.model.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/testPillReminder.xml"})
public class GeneratePillReminderIT {

	@Autowired
	private PillReminderService pillReminderService;
	
	@Test
	public void testAddPillReminder(){
		assertTrue(true);
		
// Uncomment the following code and run as a test to add a sample pill reminder
		
//		PillReminder reminder = new PillReminder();
//		reminder.setExternalId("10");
//		
//		Medicine medicine = new Medicine();
//		medicine.setName("Tylenol");
//		reminder.getMedicines().add(medicine);
//		medicine = new Medicine();
//		medicine.setName("Aspirin");
//		reminder.getMedicines().add(medicine);
//		
//		DateTime now = new DateTime();
//		Schedule schedule = new Schedule();
//		DateTime start = now.plusMinutes(1);
//		DateTime end = start.plusHours(1);
//		Time startCallTime = new Time(start.getHourOfDay(), start.getMinuteOfHour());
//		Time endCallTime = new Time(end.getHourOfDay(), end.getMinuteOfHour());
//		schedule.setStartCallTime(startCallTime);
//		schedule.setWindowStart(startCallTime);
//		schedule.setEndCallTime(endCallTime);
//		schedule.setWindowEnd(endCallTime);
//		schedule.setRepeatCount(2);
//		schedule.setRepeatInterval(600);
//		reminder.getSchedules().add(schedule);
//		
//		reminder.setStartDate(DateUtils.truncate(new Date(), Calendar.DATE));
//		reminder.setEndDate(DateUtils.addDays(new Date(),+1));
//		
//		pillReminderService.addPillReminder(reminder);
	}
}
