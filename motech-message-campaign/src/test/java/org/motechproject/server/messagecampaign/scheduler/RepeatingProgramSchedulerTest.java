package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.CampaignBuilder;
import org.motechproject.server.messagecampaign.builder.EnrollRequestBuilder;
import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatingCampaign;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RepeatingProgramSchedulerTest {

    public static final String MESSAGE_CAMPAIGN_EVENT_SUBJECT = "org.motechproject.server.messagecampaign.created-campaign-message";
    private MotechSchedulerService schedulerService;

    @Before
    public void setUp() {
        schedulerService = mock(MotechSchedulerService.class);
        initMocks(this);
    }

    @Test
    public void shouldScheduleJobs() {
        EnrollRequest request = new EnrollRequestBuilder().withDefaults().build();
        RepeatingCampaign campaign = new CampaignBuilder().defaultRepeatingCampaign();

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(schedulerService, request, campaign);

        repeatingProgramScheduler.scheduleJobs();
        ArgumentCaptor<CronSchedulableJob> capture = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(schedulerService, times(4)).scheduleJob(capture.capture());

        List<CronSchedulableJob> allJobs = capture.getAllValues();

        LocalDate jobDate = request.referenceDate();
        assertJob(allJobs.get(0), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-1-1", "child-info-week-1-1", jobDate.toDate());
        assertJob(allJobs.get(1), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-2-1", "child-info-week-2-1", jobDate.plusDays(7).toDate());
        assertJob(allJobs.get(2), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-1-2", "child-info-week-1-2", jobDate.toDate());
        assertJob(allJobs.get(3), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-2-2", "child-info-week-2-2", jobDate.plusDays(12).toDate());
    }

    @Test
    public void shouldRescheduleJobs() {
        EnrollRequest request = new EnrollRequestBuilder().withDefaults().build();
        RepeatingCampaign campaign = new CampaignBuilder().defaultRepeatingCampaign();

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(schedulerService, request, campaign);

        repeatingProgramScheduler.rescheduleJobs();
        ArgumentCaptor<CronSchedulableJob> capture = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(schedulerService, times(1)).unscheduleAllJobs("org.motechproject.server.messagecampaign.testCampaign.12345");
        verify(schedulerService, times(4)).scheduleJob(capture.capture());

        List<CronSchedulableJob> allJobs = capture.getAllValues();

        LocalDate jobDate = request.referenceDate();
        assertJob(allJobs.get(0), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-1-1", "child-info-week-1-1", jobDate.toDate());
        assertJob(allJobs.get(1), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-2-1", "child-info-week-2-1", jobDate.plusDays(7).toDate());
        assertJob(allJobs.get(2), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-1-2", "child-info-week-1-2", jobDate.toDate());
        assertJob(allJobs.get(3), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-2-2", "child-info-week-2-2", jobDate.plusDays(12).toDate());
    }

    private void assertJob(CronSchedulableJob cronSchedulableJob, String jobId, String messageKey, Date jobDate) {
        assertEquals("0 30/15 9-11 * * ?", cronSchedulableJob.getCronExpression());
        assertDate(jobDate, cronSchedulableJob.getStartTime());
        assertEquals(MESSAGE_CAMPAIGN_EVENT_SUBJECT, cronSchedulableJob.getMotechEvent().getSubject());
        assertMotechEvent(cronSchedulableJob, jobId, messageKey);
    }

    private void assertDate(Date expectedDate, Date actualDate) {
        DateTime expectedDateTime = new DateTime(expectedDate);
        DateTime actualDateTime = new DateTime(actualDate);
        assertEquals(expectedDateTime.getYear(), actualDateTime.getYear());
        assertEquals(expectedDateTime.getMonthOfYear(), actualDateTime.getMonthOfYear());
        assertEquals(expectedDateTime.getDayOfMonth(), actualDateTime.getDayOfMonth());
    }

    private void assertMotechEvent(CronSchedulableJob cronSchedulableJob, String expectedJobId, Object messageKey) {
        assertEquals(expectedJobId, cronSchedulableJob.getMotechEvent().getParameters().get("JobID"));
        assertEquals("testCampaign", cronSchedulableJob.getMotechEvent().getParameters().get("CampaignName"));
        assertEquals("12345", cronSchedulableJob.getMotechEvent().getParameters().get("ExternalID"));
        assertEquals(messageKey, cronSchedulableJob.getMotechEvent().getParameters().get("MessageKey"));
    }
}
