package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.CampaignBuilder;
import org.motechproject.server.messagecampaign.builder.EnrollRequestBuilder;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatingCampaign;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RepeatingProgramSchedulerTest {

    public static final String MESSAGE_CAMPAIGN_EVENT_SUBJECT = "org.motechproject.server.messagecampaign.send-campaign-message";
    private MotechSchedulerService schedulerService;

    @Before
    public void setUp() {
        schedulerService = mock(MotechSchedulerService.class);
        initMocks(this);
    }

    @Test
    public void shouldScheduleJobs() {
        CampaignRequest request = new EnrollRequestBuilder().withDefaults().build();
        request.setReferenceDate(DateUtil.today().plusDays(1));
        RepeatingCampaign campaign = new CampaignBuilder().defaultRepeatingCampaign();

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(schedulerService, request, campaign);

        repeatingProgramScheduler.start();
        ArgumentCaptor<RunOnceSchedulableJob> capture = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(schedulerService, times(4)).scheduleRunOnceJob(capture.capture());

        List<RunOnceSchedulableJob> allJobs = capture.getAllValues();

        LocalDate jobDate = request.referenceDate();
        assertJob(allJobs.get(0), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-1-1", "child-info-week-1-1", jobDate.toDate());
        assertJob(allJobs.get(1), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-2-1", "child-info-week-2-1", jobDate.plusDays(7).toDate());
        assertJob(allJobs.get(2), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-1-2", "child-info-week-1-2", jobDate.toDate());
        assertJob(allJobs.get(3), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-2-2", "child-info-week-2-2", jobDate.plusDays(12).toDate());
    }

    @Test
    public void shouldRescheduleJobs() {
        CampaignRequest request = new EnrollRequestBuilder().withDefaults().build();
        request.setReferenceDate(DateUtil.today().plusDays(1));
        RepeatingCampaign campaign = new CampaignBuilder().defaultRepeatingCampaign();

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(schedulerService, request, campaign);

        repeatingProgramScheduler.restart();
        ArgumentCaptor<RunOnceSchedulableJob> capture = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(schedulerService, times(1)).unscheduleAllJobs("org.motechproject.server.messagecampaign.testCampaign.12345");
        verify(schedulerService, times(4)).scheduleRunOnceJob(capture.capture());

        List<RunOnceSchedulableJob> allJobs = capture.getAllValues();

        LocalDate jobDate = request.referenceDate();
        assertJob(allJobs.get(0), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-1-1", "child-info-week-1-1", jobDate.toDate());
        assertJob(allJobs.get(1), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-2-1", "child-info-week-2-1", jobDate.plusDays(7).toDate());
        assertJob(allJobs.get(2), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-1-2", "child-info-week-1-2", jobDate.toDate());
        assertJob(allJobs.get(3), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-2-2", "child-info-week-2-2", jobDate.plusDays(12).toDate());
    }

    private void assertJob(RunOnceSchedulableJob runOnceSchedulableJob, String jobId, String messageKey, Date jobDate) {
        assertDate(jobDate, runOnceSchedulableJob.getStartDate());
        assertEquals(MESSAGE_CAMPAIGN_EVENT_SUBJECT, runOnceSchedulableJob.getMotechEvent().getSubject());
        assertMotechEvent(runOnceSchedulableJob, jobId, messageKey);
    }

    private void assertDate(Date expectedDate, Date actualDate) {
        DateTime expectedDateTime = new DateTime(expectedDate);
        DateTime actualDateTime = new DateTime(actualDate);
        assertEquals(expectedDateTime.getYear(), actualDateTime.getYear());
        assertEquals(expectedDateTime.getMonthOfYear(), actualDateTime.getMonthOfYear());
        assertEquals(expectedDateTime.getDayOfMonth(), actualDateTime.getDayOfMonth());
    }

    private void assertMotechEvent(RunOnceSchedulableJob runOnceSchedulableJob, String expectedJobId, Object messageKey) {
        assertEquals(expectedJobId, runOnceSchedulableJob.getMotechEvent().getParameters().get("JobID"));
        assertEquals("testCampaign", runOnceSchedulableJob.getMotechEvent().getParameters().get("CampaignName"));
        assertEquals("12345", runOnceSchedulableJob.getMotechEvent().getParameters().get("ExternalID"));
        assertEquals(messageKey, runOnceSchedulableJob.getMotechEvent().getParameters().get("MessageKey"));
    }
}
