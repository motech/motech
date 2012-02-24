package org.motechproject.appointments.api.dao;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.appointments.api.model.jobs.VisitReminderJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.util.DateUtil;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllVisitReminderJobsTest {

    @Mock
    private MotechSchedulerService schedulerService;

    private AllVisitReminderJobs allVisitReminderJobs;

    private Visit visit;

    private LocalDate today = DateUtil.today();

    public AllVisitReminderJobsTest() {
        visit = new Visit();
        visit.appointment(new Appointment());
        visit.reminder(reminderStartingToday());
    }

    private Reminder reminderStartingToday() {
        final Reminder reminder = new Reminder();
        reminder.startDate(today.toDate());
        reminder.endDate(today.plusDays(2).toDate());
        return reminder;
    }

    @Before
    public void setUp() {
        initMocks(this);
        allVisitReminderJobs = new AllVisitReminderJobs(schedulerService);
    }

    @Test
    public void shouldScheduleReminderJobForAVisit() {
        String externalId = "externalId";

        VisitReminderJob expectedJobForVisit = new VisitReminderJob(externalId, today.toDate(), visit);
        allVisitReminderJobs.add(visit, externalId);
        verify(schedulerService).safeScheduleJob(expectedJobForVisit);
    }
}