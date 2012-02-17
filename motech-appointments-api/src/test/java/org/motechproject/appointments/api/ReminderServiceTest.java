package org.motechproject.appointments.api;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.appointments.api.dao.AllReminders;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.util.DateUtil;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReminderServiceTest {

    @Mock
    private MotechSchedulerService schedulerService;
    @Mock
    private AllReminders allReminders;

    ReminderService reminderService;

    @Before
    public void setUp() {
        initMocks(this);
        reminderService = new ReminderService(schedulerService, allReminders);
    }

    @Test
    public void testAddReminder() {
        Reminder reminder = new Reminder();
        reminder.setReminderSubjectId("aID");
        reminder.setStartDate(DateUtil.now().toDate());

        reminderService.addReminder(reminder);

        verify(allReminders).add(reminder);
        verify(schedulerService).safeScheduleRunOnceJob(Matchers.<RunOnceSchedulableJob>any());
    }
}
