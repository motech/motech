package org.motechproject.appointments.api.dao;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.appointments.api.model.jobs.AppointmentReminderJob;
import org.motechproject.appointments.api.model.jobs.VisitReminderJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.util.DateUtil;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllReminderJobsTest {

    @Mock
    private MotechSchedulerService schedulerService;

    private AllReminderJobs allReminderJobs;

    private Visit visit;

    private LocalDate today = DateUtil.today();

    public AllReminderJobsTest() {
        visit = new Visit().name("visit");
        visit.appointment(new Appointment().reminder(reminderStartingToday()));
        visit.reminder(reminderStartingToday());
    }

    @Before
    public void setUp() {
        initMocks(this);
        allReminderJobs = new AllReminderJobs(schedulerService);
    }

    @Test
    public void shouldScheduleReminderJobForAnAppointment() {
        String externalId = "externalId";

        AppointmentReminderJob expectedJobForAppointment = new AppointmentReminderJob(externalId, visit);
        allReminderJobs.addAppointmentJob(externalId, visit);
        verify(schedulerService).safeScheduleJob(expectedJobForAppointment);
    }

    @Test
    public void shouldNotScheduleReminderJobForAnAppointment_IfNoAppointmentExistsForAVisit() {
        String externalId = "externalId";

        visit.appointment(null);
        allReminderJobs.addAppointmentJob(externalId, visit);
        verifyZeroInteractions(schedulerService);
    }

    @Test
    public void shouldScheduleReminderJobForAVisit() {
        String externalId = "externalId";

        VisitReminderJob expectedJobForVisit = new VisitReminderJob(externalId, visit);
        allReminderJobs.addVisitJob(externalId, visit);
        verify(schedulerService).safeScheduleJob(expectedJobForVisit);
    }

    @Test
    public void shouldUnscheduleReminderJobForAnAppointment() {
        String externalId = "externalId";

        allReminderJobs.removeAppointmentJob(externalId, visit);
        verify(schedulerService).safeUnscheduleJob(AppointmentReminderJob.SUBJECT, externalId + "visit");
    }

    @Test
    public void shouldUnscheduleReminderJobForAVisit() {
        String externalId = "externalId";

        allReminderJobs.removeVisitJob(externalId, visit);
        verify(schedulerService).safeUnscheduleJob(VisitReminderJob.SUBJECT, externalId + "visit");
    }

    @Test
    public void shouldRescheduleReminderJobsForAnAppointment() {
        String externalId = "externalId";

        AppointmentReminderJob expectedJobForAppointment = new AppointmentReminderJob(externalId, visit);
        allReminderJobs.rescheduleAppointmentJob(externalId, visit);
        verify(schedulerService).safeUnscheduleJob(AppointmentReminderJob.SUBJECT, externalId + "visit");
        verify(schedulerService).safeScheduleJob(expectedJobForAppointment);
    }

    @Test
    public void shouldRescheduleReminderJobsForAVisit() {
        String externalId = "externalId";

        VisitReminderJob expectedJobForVisit = new VisitReminderJob(externalId, visit);
        allReminderJobs.rescheduleVisitJob(externalId, visit);
        verify(schedulerService).safeUnscheduleJob(VisitReminderJob.SUBJECT, externalId + "visit");
        verify(schedulerService).safeScheduleJob(expectedJobForVisit);
    }

    @Test
    public void shouldRemoveAllAppointmentAndVisitJobs() {
        String externalId = "externalId";

        allReminderJobs.removeAll(externalId);
        verify(schedulerService).unscheduleAllJobs(AppointmentReminderJob.SUBJECT + externalId);
        verify(schedulerService).unscheduleAllJobs(VisitReminderJob.SUBJECT + externalId);
    }

    private Reminder reminderStartingToday() {
        final Reminder reminder = new Reminder();
        reminder.startDate(today.toDate());
        reminder.endDate(today.plusDays(2).toDate());
        return reminder;
    }
}