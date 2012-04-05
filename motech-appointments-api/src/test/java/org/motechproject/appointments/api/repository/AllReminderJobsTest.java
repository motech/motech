package org.motechproject.appointments.api.repository;

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

import java.util.Arrays;

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
        visit.appointment(new Appointment().reminders(Arrays.asList(reminderStartingToday(2), reminderStartingToday(4))));
        visit.reminder(reminderStartingToday(2));
    }

    @Before
    public void setUp() {
        initMocks(this);
        allReminderJobs = new AllReminderJobs(schedulerService);
    }

    @Test
    public void shouldScheduleReminderJobForAnAppointment() {
        String externalId = "externalId";

        AppointmentReminderJob expectedJobForAppointment1 = new AppointmentReminderJob(externalId, AppointmentReminderJob.getJobIdUsing(externalId, visit.name(), 0),
                visit.appointmentReminders().get(0), visit.name());
        AppointmentReminderJob expectedJobForAppointment2 = new AppointmentReminderJob(externalId, AppointmentReminderJob.getJobIdUsing(externalId, visit.name(), 1),
                visit.appointmentReminders().get(1), visit.name());
        allReminderJobs.addAppointmentJob(externalId, visit);
        verify(schedulerService).safeScheduleJob(expectedJobForAppointment1);
        verify(schedulerService).safeScheduleJob(expectedJobForAppointment2);
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
        verify(schedulerService).unscheduleAllJobs(AppointmentReminderJob.SUBJECT + externalId + "visit");
    }

    @Test
    public void shouldUnscheduleReminderJobForAVisit() {
        String externalId = "externalId";

        allReminderJobs.removeVisitJob(externalId, visit);
        verify(schedulerService).unscheduleAllJobs(VisitReminderJob.SUBJECT + externalId + "visit");
    }

    @Test
    public void shouldRescheduleReminderJobsForAnAppointment() {
        String externalId = "externalId";

        String jobId = AppointmentReminderJob.getJobIdUsing(externalId, visit.name(), 0);
        AppointmentReminderJob expectedJobForAppointment = new AppointmentReminderJob(externalId, jobId, visit.appointmentReminders().get(0), visit.name());
        allReminderJobs.rescheduleAppointmentJob(externalId, visit);
        verify(schedulerService).unscheduleAllJobs(AppointmentReminderJob.SUBJECT + externalId + "visit");
        verify(schedulerService).safeScheduleJob(expectedJobForAppointment);
    }

    @Test
    public void shouldRescheduleReminderJobsForAVisit() {
        String externalId = "externalId";

        VisitReminderJob expectedJobForVisit = new VisitReminderJob(externalId, visit);
        allReminderJobs.rescheduleVisitJob(externalId, visit);
        verify(schedulerService).unscheduleAllJobs(VisitReminderJob.SUBJECT + externalId + "visit");
        verify(schedulerService).safeScheduleJob(expectedJobForVisit);
    }

    @Test
    public void shouldRemoveAllAppointmentAndVisitJobs() {
        String externalId = "externalId";

        allReminderJobs.removeAll(externalId);
        verify(schedulerService).unscheduleAllJobs(AppointmentReminderJob.SUBJECT + externalId);
        verify(schedulerService).unscheduleAllJobs(VisitReminderJob.SUBJECT + externalId);
    }

    private Reminder reminderStartingToday(int days) {
        final Reminder reminder = new Reminder();
        reminder.startDate(today.toDate());
        reminder.endDate(today.plusDays(days).toDate());
        return reminder;
    }
}