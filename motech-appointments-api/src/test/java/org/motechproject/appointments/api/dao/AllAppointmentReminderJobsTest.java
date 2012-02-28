package org.motechproject.appointments.api.dao;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.util.DateUtil;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllAppointmentReminderJobsTest {
    @Mock
    private MotechSchedulerService schedulerService;
    private AllAppointmentReminderJobs allAppointmentReminderJobs;

    @Before
    public void setUp() {
        initMocks(this);
        allAppointmentReminderJobs = new AllAppointmentReminderJobs(schedulerService);
    }

    @Test
    public void shouldScheduleJobsForAppointmentReminders() {
        LocalDate startDate = DateUtil.newDate(2010, 10, 10);
        LocalDate endDate = DateUtil.newDate(2010, 10, 20);
        Reminder reminder = new Reminder().startDate(startDate.toDate()).endDate(endDate.toDate()).intervalSeconds(3600).repeatCount(2);
        DateTime dueDate = DateUtil.newDateTime(endDate.toDate());
        Appointment appointment = new Appointment().dueDate(dueDate).reminder(reminder);
        String externalId = "externalId";

        allAppointmentReminderJobs.add(appointment, externalId);
        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);

        verify(schedulerService, times(2)).safeScheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobs = cronSchedulableJobArgumentCaptor.getAllValues();

        CronSchedulableJob approachingDueDateJob = cronSchedulableJobs.get(0);

        Map<String, Object> eventParameters1 = approachingDueDateJob.getMotechEvent().getParameters();
        assertEquals(startDate.toDate(), approachingDueDateJob.getStartTime());
        assertEquals(endDate.toDate(), approachingDueDateJob.getEndTime());
        assertEquals(externalId, eventParameters1.get(EventKeys.EXTERNAL_ID_KEY));
        assertEquals("org.motechproject.appointments.api.Appointment.Reminder-" + appointment.id(), eventParameters1.get(MotechSchedulerService.JOB_ID_KEY));

        CronSchedulableJob dayOfDueDateJob = cronSchedulableJobs.get(1);

        Map<String, Object> eventParameters2 = dayOfDueDateJob.getMotechEvent().getParameters();
        assertEquals(dueDate.plusDays(1).toDate(), dayOfDueDateJob.getStartTime());
        assertEquals(dueDate.plusDays(1).toDate(), dayOfDueDateJob.getEndTime());
        assertEquals(externalId, eventParameters2.get(EventKeys.EXTERNAL_ID_KEY));
        assertEquals("org.motechproject.appointments.api.Appointment.DayAfterDueDate-" + appointment.id(), eventParameters2.get(MotechSchedulerService.JOB_ID_KEY));
    }

    @Test
    public void remove_shouldUnscheduleRepeatingJobs() {
        allAppointmentReminderJobs.remove("externalId");

        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        verify(schedulerService).safeUnscheduleJob(subjectCaptor.capture(), eq("externalId"));

        assertEquals("org.motechproject.appointments.api.Appointment.Reminder", subjectCaptor.getValue());
    }
}
