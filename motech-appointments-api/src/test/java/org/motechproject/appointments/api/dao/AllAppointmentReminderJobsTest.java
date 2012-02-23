package org.motechproject.appointments.api.dao;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.util.DateUtil;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
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
        Appointment appointment = new Appointment().dueDate(DateUtil.newDateTime(endDate.toDate())).reminder(reminder);
        String externalId = "externalId";

        allAppointmentReminderJobs.add(appointment, externalId);
        ArgumentCaptor<RepeatingSchedulableJob> repeatingSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);

        verify(schedulerService).safeScheduleRepeatingJob(repeatingSchedulableJobArgumentCaptor.capture());
        RepeatingSchedulableJob repeatingSchedulableJob = repeatingSchedulableJobArgumentCaptor.getValue();
        Map<String, Object> eventParameters = repeatingSchedulableJob.getMotechEvent().getParameters();

        assertEquals(2, repeatingSchedulableJob.getRepeatCount().intValue());
        assertEquals(startDate.toDate(), repeatingSchedulableJob.getStartTime());
        assertEquals(endDate.toDate(), repeatingSchedulableJob.getEndTime());
        assertEquals(externalId, eventParameters.get(EventKeys.EXTERNAL_ID_KEY));
        assertEquals(appointment.id(), eventParameters.get(MotechSchedulerService.JOB_ID_KEY));
    }

    @Test
    public void remove_shouldUnscheduleRepeatingJobs() {
        allAppointmentReminderJobs.remove("externalId");

        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        verify(schedulerService).safeUnscheduleRepeatingJob(subjectCaptor.capture(), eq("externalId"));

        assertEquals("org.motechproject.appointments.api.reminder", subjectCaptor.getValue());
    }
}
