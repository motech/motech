package org.motechproject.appointments.api.dao;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllReminderJobsTest {
    @Mock
    private MotechSchedulerService schedulerService;
    private AllReminderJobs allReminderJobs;

    @Before
    public void setUp() {
        initMocks(this);
        allReminderJobs = new AllReminderJobs(schedulerService);
    }

    @Test
    public void shouldScheduleJobsForAppointmentReminders() {
        Date startDate = DateUtil.newDate(2010, 10, 10).toDate();
        Date endDate = DateUtil.newDate(2010, 10, 20).toDate();
        Reminder reminder = new Reminder().startDate(startDate).endDate(endDate).intervalSeconds(3600).repeatCount(2);
        String externalId = "externalId";

        allReminderJobs.add(reminder, externalId);
        ArgumentCaptor<RepeatingSchedulableJob> repeatingSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);

        verify(schedulerService).safeScheduleRepeatingJob(repeatingSchedulableJobArgumentCaptor.capture());
        RepeatingSchedulableJob repeatingSchedulableJob = repeatingSchedulableJobArgumentCaptor.getValue();
        Map<String, Object> eventParameters = repeatingSchedulableJob.getMotechEvent().getParameters();

        assertEquals(2, repeatingSchedulableJob.getRepeatCount().intValue());
        assertEquals(startDate, repeatingSchedulableJob.getStartTime());
        assertEquals(endDate, repeatingSchedulableJob.getEndTime());
        assertNotNull(eventParameters.get(EventKeys.REMINDER_ID_KEY));
        assertEquals(externalId, eventParameters.get(EventKeys.EXTERNAL_ID_KEY));
        assertEquals("org.motechproject.appointments.api.reminder-externalId", eventParameters.get(EventKeys.JOB_ID_KEY));
    }
}
