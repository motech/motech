package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.builder.CampaignBuilder;
import org.motechproject.server.messagecampaign.builder.CampaignMessageBuilder;
import org.motechproject.server.messagecampaign.builder.EnrollRequestBuilder;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatingCampaign;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.server.messagecampaign.scheduler.RepeatingProgramScheduler.DEFAULT_INTERVAL_OFFSET;
import static org.motechproject.server.messagecampaign.scheduler.RepeatingProgramScheduler.INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT;

public class RepeatingProgramSchedulerTest {

    private MotechSchedulerService schedulerService;

    @Before
    public void setUp() {
        schedulerService = mock(MotechSchedulerService.class);
        initMocks(this);
    }

    @Test
    public void shouldScheduleJobsForTwoWeekMaxDurationWithCalendarDayOfWeekAsMonday() {
        Integer startOffset = 1;
        RepeatingCampaign campaign = new CampaignBuilder().defaultRepeatingCampaign("2 Weeks");
        CampaignRequest request = defaultBuilder().withReferenceDate(new LocalDate(2011, 11, 22)).withStartOffset(startOffset).build();

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(schedulerService, request, campaign);
        repeatingProgramScheduler.start();
        ArgumentCaptor<RepeatingSchedulableJob> capture = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(4)).scheduleRepeatingJob(capture.capture());

        Date startJobDate = request.referenceDate().toDate();
        Date jobEndDateForRepeatInterval1 = date(2011, 12, 5);
        Date jobEndDateForRepeatInterval2 = date(2011, 12, 5);
        Date jobEndDateForWeekSchedule = date(2011, 12, 5);
        Date jobEndDateForCalWeekSchedule = date(2011, 12, 4);

        List<RepeatingSchedulableJob> jobs = capture.getAllValues();
        assertJob(jobs.get(0), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-1", "child-info-week-{Offset}-1",
                startJobDate, jobEndDateForRepeatInterval1, DEFAULT_INTERVAL_OFFSET);
        assertJob(jobs.get(1), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-2", "child-info-week-{Offset}-2",
                startJobDate, jobEndDateForRepeatInterval2, DEFAULT_INTERVAL_OFFSET);
        assertJob(jobs.get(2), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-{WeekDay}", "child-info-week-{Offset}-{WeekDay}",
                startJobDate, jobEndDateForWeekSchedule, startOffset);
        assertJob(jobs.get(3), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-{WeekDay}", "child-info-week-{Offset}-{WeekDay}",
                startJobDate, jobEndDateForCalWeekSchedule, startOffset);
    }

    @Test
    public void shouldScheduleJobsForOneWeekMaxDurationWithCalendarDayOfWeekAsMonday() {
        Integer startOffset = 1;
        RepeatingCampaign campaign = new CampaignBuilder().defaultRepeatingCampaign("1 Weeks");
        CampaignRequest request = defaultBuilder().withReferenceDate(new LocalDate(2011, 11, 22)).withStartOffset(startOffset).build();

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(schedulerService, request, campaign);

        repeatingProgramScheduler.start();
        ArgumentCaptor<RepeatingSchedulableJob> capture = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(4)).scheduleRepeatingJob(capture.capture());

        Date startJobDate = request.referenceDate().toDate();
        Date jobEndDateForRepeatInterval1 = date(2011, 11, 28);
        Date jobEndDateForRepeatInterval2 = date(2011, 11, 28);
        Date jobEndDateForWeekSchedule = date(2011, 11, 28);
        Date jobEndDateForCalWeekSchedule = date(2011, 11, 27);

        List<RepeatingSchedulableJob> jobs = capture.getAllValues();
        assertJob(jobs.get(0), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-1", "child-info-week-{Offset}-1",
                startJobDate, jobEndDateForRepeatInterval1, DEFAULT_INTERVAL_OFFSET);
        assertJob(jobs.get(1), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-2", "child-info-week-{Offset}-2",
                startJobDate, jobEndDateForRepeatInterval2, DEFAULT_INTERVAL_OFFSET);
        assertJob(jobs.get(2), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-{WeekDay}", "child-info-week-{Offset}-{WeekDay}",
                startJobDate, jobEndDateForWeekSchedule, startOffset);
        assertJob(jobs.get(3), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-{WeekDay}", "child-info-week-{Offset}-{WeekDay}",
                startJobDate, jobEndDateForCalWeekSchedule, startOffset);
    }

    @Test
    public void shouldScheduleJobsForFiveWeeksAsMaxDurationWithCalendarDayOfWeekAsMonday() {
        Integer startOffset = 2;
        CampaignRequest request = defaultBuilder().withReferenceDate(new LocalDate(2011, 11, 22)).withStartOffset(startOffset).build();
        RepeatingCampaign campaign = new CampaignBuilder().defaultRepeatingCampaign("5 Weeks");

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(schedulerService, request, campaign);
        repeatingProgramScheduler.start();

        ArgumentCaptor<RepeatingSchedulableJob> capture = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(4)).scheduleRepeatingJob(capture.capture());

        Date startJobDate = request.referenceDate().toDate();
        Date jobEndDateForRepeatInterval1 = date(2011, 12, 26);
        Date jobEndDateForRepeatInterval2 = date(2011, 12, 26);
        Date jobEndDateForWeekSchedule = date(2011, 12, 19);
        Date jobEndDateForCalWeekSchedule = date(2011, 12, 18);

        List<RepeatingSchedulableJob> jobs = capture.getAllValues();
        assertJob(jobs.get(0), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-1", "child-info-week-{Offset}-1",
                startJobDate, jobEndDateForRepeatInterval1, DEFAULT_INTERVAL_OFFSET);
        assertJob(jobs.get(1), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-2", "child-info-week-{Offset}-2",
                startJobDate, jobEndDateForRepeatInterval2, DEFAULT_INTERVAL_OFFSET);
        assertJob(jobs.get(2), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-{WeekDay}", "child-info-week-{Offset}-{WeekDay}",
                startJobDate, jobEndDateForWeekSchedule, startOffset);
        assertJob(jobs.get(3), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-{WeekDay}", "child-info-week-{Offset}-{WeekDay}",
                startJobDate, jobEndDateForCalWeekSchedule, startOffset);
    }

    @Test
    public void shouldRescheduleJobs() {
        Integer startOffset = 1;
        RepeatingCampaign campaign = new CampaignBuilder().defaultRepeatingCampaign("2 Weeks");

        CampaignRequest request = defaultBuilder().withReferenceDate(new LocalDate(2011, 11, 22)).withStartOffset(startOffset).build();
        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(schedulerService, request, campaign);
        repeatingProgramScheduler.restart();

        ArgumentCaptor<RepeatingSchedulableJob> capture = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(1)).unscheduleAllJobs("org.motechproject.server.messagecampaign.testCampaign.12345");
        verify(schedulerService, times(4)).scheduleRepeatingJob(capture.capture());

        LocalDate startJobDate = request.referenceDate();
        Date jobEndDateForRepeatInterval1 = date(2011, 12, 5);
        Date jobEndDateForRepeatInterval2 = date(2011, 12, 5);
        Date jobEndDateForWeekSchedule = date(2011, 12, 5);
        Date jobEndDateForCalWeekSchedule = date(2011, 12, 4);

        List<RepeatingSchedulableJob> jobs = capture.getAllValues();
        assertJob(jobs.get(0), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-1", "child-info-week-{Offset}-1",
                startJobDate.toDate(), jobEndDateForRepeatInterval1, DEFAULT_INTERVAL_OFFSET);
        assertJob(jobs.get(1), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-2", "child-info-week-{Offset}-2",
                startJobDate.toDate(), jobEndDateForRepeatInterval2, DEFAULT_INTERVAL_OFFSET);
        assertJob(jobs.get(2), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-{WeekDay}", "child-info-week-{Offset}-{WeekDay}",
                startJobDate.toDate(), jobEndDateForWeekSchedule, startOffset);
        assertJob(jobs.get(3), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-{WeekDay}", "child-info-week-{Offset}-{WeekDay}",
                startJobDate.toDate(), jobEndDateForCalWeekSchedule, startOffset);
    }

    @Test
    public void shouldSetOffsetTo1_ForCampaignMessageWithRepeatInterval() {

        final RepeatingCampaignMessage messageWeeks = new CampaignMessageBuilder().repeatingCampaignMessageForInterval("OM1", "1 Weeks", "child-info-week-{Offset}-1");
        final RepeatingCampaignMessage messageDays = new CampaignMessageBuilder().repeatingCampaignMessageForInterval("OM1", "10 Days", "child-info-week-{Offset}-1");
        RepeatingCampaign campaign = new CampaignBuilder().repeatingCampaign("C", "2 Weeks", asList(messageWeeks, messageDays));

        int startOffset = 2;
        CampaignRequest request = defaultBuilder().withReferenceDate(new LocalDate(2011, 11, 28)).withStartOffset(startOffset).build();
        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(schedulerService, request, campaign);
        repeatingProgramScheduler.start();

        ArgumentCaptor<RepeatingSchedulableJob> capture = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(2)).scheduleRepeatingJob(capture.capture());

        List<RepeatingSchedulableJob> jobs = capture.getAllValues();
        assertEquals(jobs.get(0).getMotechEvent().getParameters().get(EventKeys.REPEATING_START_OFFSET), 1);
        assertEquals(jobs.get(1).getMotechEvent().getParameters().get(EventKeys.REPEATING_START_OFFSET), 1);
    }
    
    @Test
    public void shouldNotThrowError_ForCampaignMessageStartAndEndDaysAreSameBasedOnWeekOffsetAndReferenceDate() {

        final RepeatingCampaignMessage weekDays = new CampaignMessageBuilder().repeatingCampaignMessageForDaysApplicable("OM1", asList("Monday", "Friday"), "child-info-week-{Offset}-{WeekDay}");
        final RepeatingCampaignMessage calendarWeek = new CampaignMessageBuilder().repeatingCampaignMessageForCalendarWeek("OM1", "Tuesday", asList("Wednesday", "Friday"), "child-info-week-{Offset}-{WeekDay}");
        RepeatingCampaign campaign = new CampaignBuilder().repeatingCampaign("testCampaign", "2 Weeks", asList(weekDays, calendarWeek));

        int startOffset = 2;
        LocalDate calendarWeekEndDate_Monday = new LocalDate(2011, 11, 28);
        CampaignRequest request = defaultBuilder().withReferenceDate(calendarWeekEndDate_Monday).withStartOffset(startOffset).build();
        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(schedulerService, request, campaign);
        repeatingProgramScheduler.start();

        ArgumentCaptor<RepeatingSchedulableJob> capture = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(2)).scheduleRepeatingJob(capture.capture());

        List<RepeatingSchedulableJob> jobs = capture.getAllValues();
        assertJob(jobs.get(0), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-{WeekDay}", "child-info-week-{Offset}-{WeekDay}",
                calendarWeekEndDate_Monday.toDate(), date(2011, 12, 4), startOffset);
        assertJob(jobs.get(1), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-{WeekDay}", "child-info-week-{Offset}-{WeekDay}",
                calendarWeekEndDate_Monday.toDate(), date(2011, 11, 28), startOffset);
    }
    
    @Test
    public void shouldThrowError_ForCampaignMessageOffsetIsMoreThanMaxDuration() {

        final RepeatingCampaignMessage weekDays = new CampaignMessageBuilder().repeatingCampaignMessageForDaysApplicable("OM1", asList("Monday", "Friday"), "child-info-week-{Offset}-{WeekDay}");
        final RepeatingCampaignMessage calendarWeek = new CampaignMessageBuilder().repeatingCampaignMessageForCalendarWeek("OM1", "Tuesday", asList("Wednesday", "Friday"), "child-info-week-{Offset}-{WeekDay}");
        RepeatingCampaign campaign = new CampaignBuilder().repeatingCampaign("testCampaign", "2 Weeks", asList(weekDays, calendarWeek));

        int startOffset = 3;
        LocalDate calendarWeekEndDate_Monday = new LocalDate(2011, 11, 28);
        CampaignRequest request = defaultBuilder().withReferenceDate(calendarWeekEndDate_Monday).withStartOffset(startOffset).build();
        try {
            RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(schedulerService, request, campaign);
            repeatingProgramScheduler.start();
            Assert.fail("should fail because of date");
        } catch (IllegalArgumentException e) {
            assertEquals("startDate (2011-11-28) is after endDate (2011-11-27) for - (" + request.toString()+")",  e.getMessage());
        }

        ArgumentCaptor<RepeatingSchedulableJob> capture = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, never()).scheduleRepeatingJob(capture.capture());
    }

    private void assertJob(RepeatingSchedulableJob actualJob, String expectedJobId, String messageKey, Date jobStartDate, Date jobEndDate, Integer startOffset) {
        assertDate(jobStartDate, actualJob.getStartTime());
        assertDate(jobEndDate, actualJob.getEndTime());
        assertEquals(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, actualJob.getMotechEvent().getSubject());
        assertMotechEvent(actualJob, expectedJobId, messageKey, startOffset);
    }

    private void assertDate(Date expectedDate, Date actualDate) {
        DateTime expectedDateTime = new DateTime(expectedDate);
        DateTime actualDateTime = new DateTime(actualDate);
        assertEquals(expectedDateTime, actualDateTime);
    }

    private void assertMotechEvent(RepeatingSchedulableJob repeatingSchedulableJob, String expectedJobId, Object messageKey, Integer startOffset) {
        assertEquals(expectedJobId, repeatingSchedulableJob.getMotechEvent().getParameters().get("JobID"));
        assertEquals("testCampaign", repeatingSchedulableJob.getMotechEvent().getParameters().get("CampaignName"));
        assertEquals("12345", repeatingSchedulableJob.getMotechEvent().getParameters().get("ExternalID"));
        assertEquals(messageKey, repeatingSchedulableJob.getMotechEvent().getParameters().get("MessageKey"));
        assertEquals(startOffset, repeatingSchedulableJob.getMotechEvent().getParameters().get("RepeatingStartOffset"));
    }

    private EnrollRequestBuilder defaultBuilder() {
        return new EnrollRequestBuilder().withDefaults();
    }

    private Date date(int year, int month, int day) {
        return new LocalDate(year, month, day).toDate();
    }
}
