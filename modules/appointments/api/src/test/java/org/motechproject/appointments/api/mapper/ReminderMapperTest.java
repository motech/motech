package org.motechproject.appointments.api.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.service.contract.ReminderConfiguration;
import org.motechproject.commons.date.util.DateUtil;

import static junit.framework.Assert.assertEquals;

public class ReminderMapperTest {

    private DateTime today;
    private ReminderMapper reminderMapper;
    private ReminderConfiguration reminderConfiguration;

    @Before
    public void setUp() {
        reminderConfiguration = new ReminderConfiguration().setRemindFrom(10).setIntervalCount(1).setIntervalUnit(ReminderConfiguration.IntervalUnit.HOURS).setRepeatCount(20);
        today = DateTime.now();
        reminderMapper = new ReminderMapper();
    }

    @Test
    public void shouldGetAReminder_Given_ReminderConfiguration() {
        Reminder reminder = reminderMapper.map(today, reminderConfiguration);

        assertEquals(today.minusDays(10).toLocalDate(), DateUtil.newDate(reminder.startDate()));
        assertEquals(3600, reminder.intervalSeconds());
        assertEquals(20, reminder.repeatCount());
    }

    @Test
    public void shouldCalculateIntervalInSeconds() {
        assertEquals(30, reminderMapper.intervalSeconds(ReminderConfiguration.IntervalUnit.SECONDS, 30));
        assertEquals(120, reminderMapper.intervalSeconds(ReminderConfiguration.IntervalUnit.MINUTES, 2));
        assertEquals(14400, reminderMapper.intervalSeconds(ReminderConfiguration.IntervalUnit.HOURS, 4));
        assertEquals(172800, reminderMapper.intervalSeconds(ReminderConfiguration.IntervalUnit.DAYS, 2));
        assertEquals(604800, reminderMapper.intervalSeconds(ReminderConfiguration.IntervalUnit.WEEKS, 1));
        assertEquals(-1, reminderMapper.intervalSeconds(null, 0));
    }
}
