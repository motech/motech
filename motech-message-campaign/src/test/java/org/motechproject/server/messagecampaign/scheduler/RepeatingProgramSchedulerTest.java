package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.CampaignBuilder;
import org.motechproject.server.messagecampaign.builder.CampaignMessageBuilder;
import org.motechproject.server.messagecampaign.builder.EnrollRequestBuilder;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatingCampaign;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.server.messagecampaign.scheduler.RepeatingProgramScheduler.INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT;
import static org.motechproject.util.DateUtil.newDateTime;

public class RepeatingProgramSchedulerTest {

    @Mock
    private MotechSchedulerService mockSchedulerService;
    @Mock
    private CampaignEnrollmentService mockCampaignEnrollmentService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldScheduleJobsForTwoWeekMaxDurationWithCalendarDayOfWeekAsMonday() {
        Integer startOffset = 1;
        Time reminderTime = new Time(8, 30);
        RepeatingCampaign campaign = new CampaignBuilder().defaultRepeatingCampaign("2 Weeks");
        LocalDate startDate = new LocalDate(2011, 11, 22);
        CampaignRequest request = defaultBuilder().withReferenceDate(startDate).withReminderTime(reminderTime).withStartOffset(startOffset).build();

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService, true);
        repeatingProgramScheduler.start();
        ArgumentCaptor<CronSchedulableJob> capture = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(mockSchedulerService, times(4)).safeScheduleJob(capture.capture());
        verify(mockSchedulerService, never()).safeScheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());

        Date startJobDate = DateUtil.newDateTime(request.referenceDate(), reminderTime).toDate();
        Date jobEndDateForRepeatInterval1 = dateAtEndOfDay(2011, 12, 5);
        Date jobEndDateForRepeatInterval2 = dateAtEndOfDay(2011, 12, 5);
        Date jobEndDateForWeekSchedule = dateAtEndOfDay(2011, 12, 5);
        Date jobEndDateForCalWeekSchedule = dateAtEndOfDay(2011, 12, 4);

        List<CronSchedulableJob> jobs = capture.getAllValues();

        assertJob(jobs.get(0), startJobDate, jobEndDateForRepeatInterval1);
        assertMotechEvent(jobs.get(0).getMotechEvent(), "MessageJob.testCampaign.12345.child-info-week-{Offset}-1", "child-info-week-{Offset}-1");

        assertJob(jobs.get(1), startJobDate, jobEndDateForRepeatInterval2);
        assertMotechEvent(jobs.get(1).getMotechEvent(), "MessageJob.testCampaign.12345.child-info-week-{Offset}-2", "child-info-week-{Offset}-2");

        assertJob(jobs.get(2), startJobDate, jobEndDateForWeekSchedule);
        assertMotechEvent(jobs.get(2).getMotechEvent(), "MessageJob.testCampaign.12345.child-info-week-{Offset}-{WeekDay}", "child-info-week-{Offset}-{WeekDay}");

        assertJob(jobs.get(3), startJobDate, jobEndDateForCalWeekSchedule);
        assertMotechEvent(jobs.get(3).getMotechEvent(), "MessageJob.testCampaign.12345.child-info-week-{Offset}-{WeekDay}", "child-info-week-{Offset}-{WeekDay}");


        verify(mockSchedulerService, times(4)).safeScheduleJob(Matchers.<CronSchedulableJob>any());
        verify(mockSchedulerService, never()).safeScheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());
    }

    @Test
    public void shouldScheduleJobsForOneWeekMaxDurationWithCalendarDayOfWeekAsMonday() {
        Integer startOffset = 1;
        Time reminderTime = new Time(9, 30);
        RepeatingCampaign campaign = new CampaignBuilder().defaultRepeatingCampaign("1 Weeks");
        CampaignRequest request = defaultBuilder().withReferenceDate(new LocalDate(2011, 11, 22)).withReminderTime(reminderTime).withStartOffset(startOffset).build();

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService, false);

        repeatingProgramScheduler.start();
        ArgumentCaptor<CronSchedulableJob> capture = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(mockSchedulerService, times(4)).safeScheduleJob(capture.capture());
        verify(mockSchedulerService, never()).safeScheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());

        Date jobEndDateForRepeatInterval1 = dateAtEndOfDay(2011, 11, 28);
        Date jobEndDateForRepeatInterval2 = dateAtEndOfDay(2011, 11, 28);
        Date jobEndDateForWeekSchedule = dateAtEndOfDay(2011, 11, 28);
        Date jobEndDateForCalWeekSchedule = dateAtEndOfDay(2011, 11, 27);

        List<CronSchedulableJob> jobs = capture.getAllValues();
        assertDate(jobs.get(0).getEndTime(), jobEndDateForRepeatInterval1);
        assertDate(jobs.get(1).getEndTime(), jobEndDateForRepeatInterval2);
        assertDate(jobs.get(2).getEndTime(), jobEndDateForWeekSchedule);
        assertDate(jobs.get(3).getEndTime(), jobEndDateForCalWeekSchedule);
    }
    
    @Test
    public void shouldReturnCampaignEndTime() {
        Integer startOffset = 1;
        Time reminderTime = new Time(9, 30);
        RepeatingCampaign campaign = new CampaignBuilder().defaultRepeatingCampaign("1 Weeks");
        LocalDate referenceDate = new LocalDate(2011, 11, 22);
        CampaignRequest request = defaultBuilder().withReferenceDate(referenceDate).withReminderTime(reminderTime).withStartOffset(startOffset).build();

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService, false);
        
        assertEquals(newDateTime(2011, 11, 29, 0, 0, 0), repeatingProgramScheduler.getCampaignEnd());
    }

    @Test
    public void shouldScheduleJobsOnlyForApplicableDaysWithCalendarDayOfWeekAsMonday() {
        Integer startOffset = 1;
        Time reminderTime = new Time(9, 30);
        RepeatingCampaignMessage campaignMessage = new CampaignMessageBuilder().repeatingCampaignMessageForDaysApplicable("OM2", asList("Monday", "Wednesday"), "child-info-week-{Offset}-{WeekDay}").deliverTime(new Time(10, 30));
        RepeatingCampaign campaign = new CampaignBuilder().repeatingCampaign("campaignName", "2 Weeks", asList(campaignMessage));
        CampaignRequest request = defaultBuilder().withReferenceDate(new LocalDate(2012, 2, 17)).withReminderTime(reminderTime).withStartOffset(startOffset).build();

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService, false);

        repeatingProgramScheduler.start();
        ArgumentCaptor<CronSchedulableJob> capture = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(mockSchedulerService, times(1)).safeScheduleJob(capture.capture());
        verify(mockSchedulerService, never()).safeScheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());

        Date interval = dateAtEndOfDay(2012, 3, 1);

        List<CronSchedulableJob> jobs = capture.getAllValues();
        assertDate(jobs.get(0).getEndTime(), interval);
        assertThat(jobs.get(0).getCronExpression(), is("0 30 10 ? * MON,WED *"));
    }

    @Test
    public void shouldScheduleJobsOnlyOnUserSpecifiedDaysWithCalendarDayOfWeekAsMonday() {
        Time reminderTime = new Time(9, 30);
        RepeatingCampaignMessage campaignMessage = new CampaignMessageBuilder().repeatingCampaignMessageForDaysApplicable("OM2", asList("Monday", "Wednesday"), "child-info-week-{Offset}-{WeekDay}").deliverTime(new Time(10, 30));
        RepeatingCampaign campaign = new CampaignBuilder().repeatingCampaign("campaignName", "2 Weeks", asList(campaignMessage));
        List<DayOfWeek> userSpecifiedDays = asList(DayOfWeek.Tuesday, DayOfWeek.Friday);
        CampaignRequest request = defaultBuilder().withReferenceDate(new LocalDate(2012, 2, 17)).withReminderTime(reminderTime).withUserSpecifiedDays(userSpecifiedDays).build();

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService, false);

        repeatingProgramScheduler.start();
        ArgumentCaptor<CronSchedulableJob> capture = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(mockSchedulerService, times(1)).safeScheduleJob(capture.capture());
        verify(mockSchedulerService, never()).safeScheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());

        Date interval = dateAtEndOfDay(2012, 3, 1);

        List<CronSchedulableJob> jobs = capture.getAllValues();
        assertDate(jobs.get(0).getEndTime(), interval);
        assertThat(jobs.get(0).getCronExpression(), is("0 30 10 ? * TUE,FRI *"));
    }


    @Test
    public void shouldScheduleJobsForFiveWeeksAsMaxDurationWithCalendarDayOfWeekAsMonday() {
        Integer startOffset = 2;
        Time reminderTime = new Time(21, 30);
        CampaignRequest request = defaultBuilder().withReferenceDate(new LocalDate(2011, 11, 22)).withReminderTime(reminderTime).withStartOffset(startOffset).build();
        RepeatingCampaign campaign = new CampaignBuilder().defaultRepeatingCampaign("5 Weeks");

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService, true);
        repeatingProgramScheduler.start();

        ArgumentCaptor<CronSchedulableJob> capture = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(mockSchedulerService, times(4)).safeScheduleJob(capture.capture());
        verify(mockSchedulerService, never()).safeScheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());

        Date startJobDate = DateUtil.newDateTime(request.referenceDate(), reminderTime).toDate();
        Date jobEndDateForRepeatInterval1 = dateAtEndOfDay(2011, 12, 26);
        Date jobEndDateForRepeatInterval2 = dateAtEndOfDay(2011, 12, 26);
        Date jobEndDateForWeekSchedule = dateAtEndOfDay(2011, 12, 19);
        Date jobEndDateForCalWeekSchedule = dateAtEndOfDay(2011, 12, 18);

        List<CronSchedulableJob> jobs = capture.getAllValues();

        assertJob(jobs.get(0), startJobDate, jobEndDateForRepeatInterval1);
        assertJob(jobs.get(1), startJobDate, jobEndDateForRepeatInterval2);
        assertJob(jobs.get(2), startJobDate, jobEndDateForWeekSchedule);
        assertJob(jobs.get(3), startJobDate, jobEndDateForCalWeekSchedule);
    }

    @Test
    public void shouldSetOffsetTo1_ForCampaignMessageWithRepeatInterval() {

        final RepeatingCampaignMessage messageWeeks = new CampaignMessageBuilder().repeatingCampaignMessageForInterval("OM1", "1 Weeks", "child-info-week-{Offset}-1", "0:0");
        final RepeatingCampaignMessage messageDays = new CampaignMessageBuilder().repeatingCampaignMessageForInterval("OM1", "10 Days", "child-info-week-{Offset}-1", "0:0");
        RepeatingCampaign campaign = new CampaignBuilder().repeatingCampaign("C", "2 Weeks", asList(messageWeeks, messageDays));

        int startOffset = 2;
        LocalDate startDate = new LocalDate(2011, 11, 28);
        CampaignRequest request = defaultBuilder().withReferenceDate(startDate).withStartOffset(startOffset).build();
        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService, false);
        repeatingProgramScheduler.start();

        verify(mockSchedulerService, times(2)).safeScheduleJob(Matchers.<CronSchedulableJob>any());
        verify(mockSchedulerService, never()).safeScheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());
    }

    @Test
    public void shouldNotThrowError_ForCampaignMessageStartAndEndDaysAreSameBasedOnWeekOffsetAndReferenceDate() {

        Time reminderTime = new Time(9, 30);
        final RepeatingCampaignMessage weekDays = new CampaignMessageBuilder().repeatingCampaignMessageForDaysApplicable("OM1", asList("Monday", "Friday"), "child-info-week-{Offset}-{WeekDay}");
        final RepeatingCampaignMessage calendarWeek = new CampaignMessageBuilder().repeatingCampaignMessageForCalendarWeek("OM1", "Tuesday", asList("Wednesday", "Friday"), "child-info-week-{Offset}-{WeekDay}");
        RepeatingCampaign campaign = new CampaignBuilder().repeatingCampaign("testCampaign", "2 Weeks", asList(weekDays, calendarWeek));

        int startOffset = 2;
        Date calendarWeekEndDate_Monday = DateUtil.newDateTime(new LocalDate(2011, 11, 28), reminderTime).toDate();
        CampaignRequest request = defaultBuilder().withReferenceDate(new LocalDate(calendarWeekEndDate_Monday)).withReminderTime(reminderTime).withStartOffset(startOffset).build();
        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService, true);
        repeatingProgramScheduler.start();

        ArgumentCaptor<CronSchedulableJob> capture = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(mockSchedulerService, times(2)).safeScheduleJob(capture.capture());
        verify(mockSchedulerService, never()).safeScheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());

        List<CronSchedulableJob> jobs = capture.getAllValues();

        assertJob(jobs.get(0), calendarWeekEndDate_Monday, dateAtEndOfDay(2011, 12, 4));
        assertJob(jobs.get(1), calendarWeekEndDate_Monday, dateAtEndOfDay(2011, 11, 28));
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
            RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService, false);
            repeatingProgramScheduler.start();
            Assert.fail("should fail because of date");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("startDate (2011-11-28) is after endDate "));
        }

        ArgumentCaptor<RepeatingSchedulableJob> capture = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(mockSchedulerService, never()).scheduleRepeatingJob(capture.capture());
        verify(mockSchedulerService, never()).safeScheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());
    }

    @Test
    public void shouldUnscheduleOnStopCampaign() {
        RepeatingCampaign campaign = new CampaignBuilder().defaultRepeatingCampaign("2 Weeks");
        CampaignRequest request = defaultBuilder().withReferenceDate(new LocalDate(2011, 11, 28)).withStartOffset(1).build();
        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService, false);
        repeatingProgramScheduler.stop();
        verify(mockSchedulerService, times(5)).safeUnscheduleJob(Matchers.<String>any(), Matchers.<String>any());
        verify(mockSchedulerService, never()).safeScheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());
    }

    @Test
    public void shouldScheduleJobForRepeatingMessageCampaignsBasedOnDeliveryTimeIfTheDeliveryStrategyIsNotWithin24Hours() {
        LocalDate localDate = new LocalDate(2011, 11, 28);
        CampaignRequest request = defaultBuilder().withReferenceDate(localDate).withStartOffset(1).build();

        Properties properties = new Properties();
        properties.setProperty("messagecampaign.definition.file", "/simple-message-campaign.json");
        AllMessageCampaigns allMessageCampaigns = new AllMessageCampaigns(properties);
        RepeatingCampaign campaign = (RepeatingCampaign) allMessageCampaigns.get("PREGNANCY");

        RepeatingProgramScheduler repeatingProgramScheduler =
                new RepeatingProgramScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService, false);

        repeatingProgramScheduler.start();

        ArgumentCaptor<CronSchedulableJob> capture = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(mockSchedulerService).safeScheduleJob(capture.capture());
        verify(mockSchedulerService, never()).safeScheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());

        List<CronSchedulableJob> jobs = capture.getAllValues();

        Date jobStartDate = DateUtil.newDateTime(new LocalDate(2011, 11, 28), new Time(10, 30)).toDate();
        Date jobEndDate = dateAtEndOfDay(2012, 9, 1);
        CronSchedulableJob actualJob = jobs.get(0);

        assertDate(jobStartDate, actualJob.getStartTime());
        assertDate(jobEndDate, actualJob.getEndTime());
        assertEquals(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, actualJob.getMotechEvent().getSubject());
        assertEquals("0 30 10 ? * MON,WED,FRI *", actualJob.getCronExpression());
        assertEquals("MessageJob.PREGNANCY.12345.PREGNANCY-cw{Offset}-{WeekDay}", actualJob.getMotechEvent().getParameters().get("JobID"));
        assertEquals("PREGNANCY", actualJob.getMotechEvent().getParameters().get("CampaignName"));
        assertEquals("12345", actualJob.getMotechEvent().getParameters().get("ExternalID"));
        assertEquals("PREGNANCY-cw{Offset}-{WeekDay}", actualJob.getMotechEvent().getParameters().get("MessageKey"));
    }

    @Test
    public void shouldScheduleJobForRepeatingMessageCampaignsBasedOnCampaignDeliveryTimeIfPresent() {
        LocalDate localDate = new LocalDate(2011, 11, 28);
        CampaignRequest request = defaultBuilder().withReferenceDate(localDate).withStartOffset(1).withUserSpecifiedDays(Arrays.asList(DayOfWeek.Sunday)).withDeliverTime(new Time(12,30)).build();

        Properties properties = new Properties();
        properties.setProperty("messagecampaign.definition.file", "/simple-message-campaign.json");
        AllMessageCampaigns allMessageCampaigns = new AllMessageCampaigns(properties);
        RepeatingCampaign campaign = (RepeatingCampaign) allMessageCampaigns.get("PREGNANCY");

        RepeatingProgramScheduler repeatingProgramScheduler =
                new RepeatingProgramScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService, false);

        repeatingProgramScheduler.start();

        ArgumentCaptor<CronSchedulableJob> capture = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(mockSchedulerService).safeScheduleJob(capture.capture());

        List<CronSchedulableJob> jobs = capture.getAllValues();

        Date expectedJobStartDate = DateUtil.newDateTime(new LocalDate(2011, 11, 28), new Time(12, 30)).toDate();
        Date jobEndDate = dateAtEndOfDay(2012, 9, 1);
        CronSchedulableJob actualJob = jobs.get(0);

        assertDate(expectedJobStartDate, actualJob.getStartTime());
        assertDate(jobEndDate, actualJob.getEndTime());
        assertDate(expectedJobStartDate, actualJob.getStartTime());
        assertDate(jobEndDate, actualJob.getEndTime());
        assertEquals(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, actualJob.getMotechEvent().getSubject());
        assertEquals("0 30 12 ? * SUN *", actualJob.getCronExpression());
        assertEquals("MessageJob.PREGNANCY.12345.PREGNANCY-cw{Offset}-{WeekDay}", actualJob.getMotechEvent().getParameters().get("JobID"));
        assertEquals("PREGNANCY", actualJob.getMotechEvent().getParameters().get("CampaignName"));
        assertEquals("12345", actualJob.getMotechEvent().getParameters().get("ExternalID"));
        assertEquals("PREGNANCY-cw{Offset}-{WeekDay}", actualJob.getMotechEvent().getParameters().get("MessageKey"));
    }

    @Test
    public void shouldScheduleJobsForOneWeekMaxDurationWithHourlyRepeatInterval() {
        Time reminderTime = new Time(8, 30);
        LocalDate startDate = new LocalDate(2011, 11, 22);
        RepeatingCampaign campaign = new CampaignBuilder().repeatingCampaignWithHourRepeatInterval("1 Week", "8:30");
        CampaignRequest request = defaultBuilder().withReferenceDate(startDate).withReminderTime(reminderTime).build();

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService, false);
        repeatingProgramScheduler.start();
        ArgumentCaptor<RepeatingSchedulableJob> capture = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(mockSchedulerService, times(2)).safeScheduleRepeatingJob(capture.capture());
        verify(mockSchedulerService, never()).safeScheduleJob(Matchers.<CronSchedulableJob>any());

        DateTime jobStartDate = DateUtil.newDateTime(request.referenceDate(), reminderTime);
        Date jobEndDate = jobStartDate.plusDays(7).toDate();

        List<RepeatingSchedulableJob> jobs = capture.getAllValues();

        assertDate(jobStartDate.toDate(), jobs.get(0).getStartTime());
        assertDate(jobEndDate, jobs.get(0).getEndTime());
        assertEquals(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, jobs.get(0).getMotechEvent().getSubject());
        assertEquals(1L * 60L * 60L * 1000L, jobs.get(0).getRepeatInterval());
        assertMotechEvent(jobs.get(0).getMotechEvent(), "MessageJob.testCampaign.12345.child-info-hour-{Offset}-1", "child-info-hour-{Offset}-1");

        assertDate(jobStartDate.toDate(), jobs.get(1).getStartTime());
        assertDate(jobEndDate, jobs.get(1).getEndTime());
        assertEquals(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, jobs.get(1).getMotechEvent().getSubject());
        assertEquals(12L * 60L * 60L * 1000L, jobs.get(1).getRepeatInterval());
        assertMotechEvent(jobs.get(1).getMotechEvent(), "MessageJob.testCampaign.12345.child-info-hour-{Offset}-2", "child-info-hour-{Offset}-2");
    }

    @Test
    public void shouldScheduleJobsForSeventeenHoursMaxDurationWithHourlyRepeatInterval() {
        Time reminderTime = new Time(8, 30);
        LocalDate startDate = new LocalDate(2011, 11, 22);
        RepeatingCampaign campaign = new CampaignBuilder().repeatingCampaignWithHourRepeatInterval("17 Hours", "8:30");
        CampaignRequest request = defaultBuilder().withReferenceDate(startDate).withReminderTime(reminderTime).build();

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService, false);
        repeatingProgramScheduler.start();
        ArgumentCaptor<RepeatingSchedulableJob> capture = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(mockSchedulerService, times(2)).safeScheduleRepeatingJob(capture.capture());
        verify(mockSchedulerService, never()).safeScheduleJob(Matchers.<CronSchedulableJob>any());

        DateTime jobStartDate = DateUtil.newDateTime(request.referenceDate(), reminderTime);
        Date jobEndDate = jobStartDate.plusHours(17).toDate();

        List<RepeatingSchedulableJob> jobs = capture.getAllValues();

        assertDate(jobStartDate.toDate(), jobs.get(0).getStartTime());
        assertDate(jobEndDate, jobs.get(0).getEndTime());
        assertEquals(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, jobs.get(0).getMotechEvent().getSubject());
        assertEquals(1L * 60L * 60L * 1000L, jobs.get(0).getRepeatInterval());
        assertMotechEvent(jobs.get(0).getMotechEvent(), "MessageJob.testCampaign.12345.child-info-hour-{Offset}-1", "child-info-hour-{Offset}-1");

        assertDate(jobStartDate.toDate(), jobs.get(1).getStartTime());
        assertDate(jobEndDate, jobs.get(1).getEndTime());
        assertEquals(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, jobs.get(1).getMotechEvent().getSubject());
        assertEquals(12L * 60L * 60L * 1000L, jobs.get(1).getRepeatInterval());
        assertMotechEvent(jobs.get(1).getMotechEvent(), "MessageJob.testCampaign.12345.child-info-hour-{Offset}-2", "child-info-hour-{Offset}-2");
    }

    @Test
    public void shouldScheduleJobsForOneWeekMaxDurationWithMinuteRepeatInterval() {
        Time reminderTime = new Time(8, 30);
        LocalDate startDate = new LocalDate(2011, 11, 22);
        RepeatingCampaign campaign = new CampaignBuilder().repeatingCampaignWithMinuteRepeatInterval("1 Week", "8:30");
        CampaignRequest request = defaultBuilder().withReferenceDate(startDate).withReminderTime(reminderTime).build();

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService, false);
        repeatingProgramScheduler.start();
        ArgumentCaptor<RepeatingSchedulableJob> capture = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(mockSchedulerService, times(2)).safeScheduleRepeatingJob(capture.capture());
        verify(mockSchedulerService, never()).safeScheduleJob(Matchers.<CronSchedulableJob>any());

        DateTime jobStartDate = DateUtil.newDateTime(request.referenceDate(), reminderTime);
        Date jobEndDate = jobStartDate.plusDays(7).toDate();

        List<RepeatingSchedulableJob> jobs = capture.getAllValues();

        assertDate(jobStartDate.toDate(), jobs.get(0).getStartTime());
        assertDate(jobEndDate, jobs.get(0).getEndTime());
        assertEquals(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, jobs.get(0).getMotechEvent().getSubject());
        assertEquals(15L * 60L * 1000L, jobs.get(0).getRepeatInterval());
        assertMotechEvent(jobs.get(0).getMotechEvent(), "MessageJob.testCampaign.12345.child-info-minute-{Offset}-1", "child-info-minute-{Offset}-1");

        assertDate(jobStartDate.toDate(), jobs.get(1).getStartTime());
        assertDate(jobEndDate, jobs.get(1).getEndTime());
        assertEquals(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, jobs.get(1).getMotechEvent().getSubject());
        assertEquals(20L * 60L * 1000L, jobs.get(1).getRepeatInterval());
        assertMotechEvent(jobs.get(1).getMotechEvent(), "MessageJob.testCampaign.12345.child-info-minute-{Offset}-2", "child-info-minute-{Offset}-2");
    }

    @Test
    public void shouldScheduleJobsForTwentyHoursMaxDurationWithMinuteRepeatInterval() {
        Time reminderTime = new Time(8, 30);
        LocalDate startDate = new LocalDate(2011, 11, 22);
        RepeatingCampaign campaign = new CampaignBuilder().repeatingCampaignWithMinuteRepeatInterval("20 Hours", "8:30");
        CampaignRequest request = defaultBuilder().withReferenceDate(startDate).withReminderTime(reminderTime).build();

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService, false);
        repeatingProgramScheduler.start();
        ArgumentCaptor<RepeatingSchedulableJob> capture = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(mockSchedulerService, times(2)).safeScheduleRepeatingJob(capture.capture());
        verify(mockSchedulerService, never()).safeScheduleJob(Matchers.<CronSchedulableJob>any());

        DateTime jobStartDate = DateUtil.newDateTime(request.referenceDate(), reminderTime);
        Date jobEndDate = jobStartDate.plusHours(20).toDate();

        List<RepeatingSchedulableJob> jobs = capture.getAllValues();

        assertDate(jobStartDate.toDate(), jobs.get(0).getStartTime());
        assertDate(jobEndDate, jobs.get(0).getEndTime());
        assertEquals(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, jobs.get(0).getMotechEvent().getSubject());
        assertEquals(15L * 60L * 1000L, jobs.get(0).getRepeatInterval());
        assertMotechEvent(jobs.get(0).getMotechEvent(), "MessageJob.testCampaign.12345.child-info-minute-{Offset}-1", "child-info-minute-{Offset}-1");

        assertDate(jobStartDate.toDate(), jobs.get(1).getStartTime());
        assertDate(jobEndDate, jobs.get(1).getEndTime());
        assertEquals(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, jobs.get(1).getMotechEvent().getSubject());
        assertEquals(20L * 60L * 1000L, jobs.get(1).getRepeatInterval());
        assertMotechEvent(jobs.get(1).getMotechEvent(), "MessageJob.testCampaign.12345.child-info-minute-{Offset}-2", "child-info-minute-{Offset}-2");
    }

    @Test
    public void shouldScheduleJobsForFiftyMinutesMaxDurationWithMinuteRepeatInterval() {
        Time reminderTime = new Time(8, 30);
        LocalDate startDate = new LocalDate(2011, 11, 22);
        RepeatingCampaign campaign = new CampaignBuilder().repeatingCampaignWithMinuteRepeatInterval("50 Minutes", "8:30");
        CampaignRequest request = defaultBuilder().withReferenceDate(startDate).withReminderTime(reminderTime).build();

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService, false);
        repeatingProgramScheduler.start();
        ArgumentCaptor<RepeatingSchedulableJob> capture = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(mockSchedulerService, times(2)).safeScheduleRepeatingJob(capture.capture());
        verify(mockSchedulerService, never()).safeScheduleJob(Matchers.<CronSchedulableJob>any());

        DateTime jobStartDate = DateUtil.newDateTime(request.referenceDate(), reminderTime);
        Date jobEndDate = jobStartDate.plusMinutes(50).toDate();

        List<RepeatingSchedulableJob> jobs = capture.getAllValues();

        assertDate(jobStartDate.toDate(), jobs.get(0).getStartTime());
        assertDate(jobEndDate, jobs.get(0).getEndTime());
        assertEquals(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, jobs.get(0).getMotechEvent().getSubject());
        assertEquals(15L * 60L * 1000L, jobs.get(0).getRepeatInterval());
        assertMotechEvent(jobs.get(0).getMotechEvent(), "MessageJob.testCampaign.12345.child-info-minute-{Offset}-1", "child-info-minute-{Offset}-1");

        assertDate(jobStartDate.toDate(), jobs.get(1).getStartTime());
        assertDate(jobEndDate, jobs.get(1).getEndTime());
        assertEquals(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, jobs.get(1).getMotechEvent().getSubject());
        assertEquals(20L * 60L * 1000L, jobs.get(1).getRepeatInterval());
        assertMotechEvent(jobs.get(1).getMotechEvent(), "MessageJob.testCampaign.12345.child-info-minute-{Offset}-2", "child-info-minute-{Offset}-2");
    }

    private void assertJob(CronSchedulableJob actualJob, Date jobStartDate, Date jobEndDate) {
        assertDate(jobStartDate, actualJob.getStartTime());
        assertDate(jobEndDate, actualJob.getEndTime());
        assertEquals(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, actualJob.getMotechEvent().getSubject());
        assertEquals(buildCronExpression(jobStartDate), actualJob.getCronExpression());
    }

    private String buildCronExpression(Date jobDate) {
        DateTime dateTime = new DateTime(jobDate);
        int hour = dateTime.get(DateTimeFieldType.hourOfDay());
        int min = dateTime.get(DateTimeFieldType.minuteOfHour());
        int sec = dateTime.get(DateTimeFieldType.secondOfMinute());

        return String.format("%d %d %d 1/1 * ? *", sec, min, hour);
    }

    private void assertDate(Date expectedDate, Date actualDate) {
        DateTime expectedDateTime = DateUtil.newDateTime(expectedDate);
        DateTime actualDateTime = DateUtil.newDateTime(actualDate);
        assertEquals(expectedDateTime, actualDateTime);
    }

    private void assertMotechEvent(MotechEvent motechEvent, String expectedJobId, Object messageKey) {
        assertEquals(expectedJobId, motechEvent.getParameters().get("JobID"));
        assertEquals("testCampaign", motechEvent.getParameters().get("CampaignName"));
        assertEquals("12345", motechEvent.getParameters().get("ExternalID"));
        assertEquals(messageKey, motechEvent.getParameters().get("MessageKey"));
    }

    private EnrollRequestBuilder defaultBuilder() {
        return new EnrollRequestBuilder().withDefaults();
    }

    private Date dateAtEndOfDay(int year, int month, int day) {
        return new DateTime(year, month, day, 23, 59, 59, 999).toDate();
    }


}
