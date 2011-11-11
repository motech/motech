package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.CampaignBuilder;
import org.motechproject.server.messagecampaign.builder.EnrollRequestBuilder;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatingCampaign;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.factory.WallTimeFactory;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RepeatingProgramSchedulerTest {

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

        WallTime duration = WallTimeFactory.create(((RepeatingCampaign) campaign).maxDuration());
        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(schedulerService, request, campaign);

        repeatingProgramScheduler.start();
        ArgumentCaptor<RepeatingSchedulableJob> capture = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(2)).scheduleRepeatingJob(capture.capture());

        RepeatingSchedulableJob job = capture.getAllValues().get(0);
        LocalDate startJobDate = request.referenceDate();
        LocalDate jobEndDate = startJobDate.plusDays(duration.inDays());
        assertJob(job, "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-1", "child-info-week-{Offset}-1",
                startJobDate.toDate(), jobEndDate.toDate());

        RepeatingSchedulableJob job2 = capture.getAllValues().get(1);
        assertJob(job2, "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-2", "child-info-week-{Offset}-2",
                startJobDate.toDate(), jobEndDate.toDate());
    }

    @Test
    public void shouldRescheduleJobs() {
        CampaignRequest request = new EnrollRequestBuilder().withDefaults().build();
        request.setReferenceDate(DateUtil.today().plusDays(1));
        RepeatingCampaign campaign = new CampaignBuilder().defaultRepeatingCampaign();

        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(schedulerService, request, campaign);

        repeatingProgramScheduler.restart();
        ArgumentCaptor<RepeatingSchedulableJob> capture = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(1)).unscheduleAllJobs("org.motechproject.server.messagecampaign.testCampaign.12345");
        verify(schedulerService, times(2)).scheduleRepeatingJob(capture.capture());

        WallTime duration = WallTimeFactory.create((campaign).maxDuration());

        RepeatingSchedulableJob job = capture.getAllValues().get(0);
        LocalDate startJobDate = request.referenceDate();
        LocalDate jobEndDate = startJobDate.plusDays(duration.inDays());
        assertJob(job, "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-1", "child-info-week-{Offset}-1",
                startJobDate.toDate(), jobEndDate.toDate());

        RepeatingSchedulableJob job2 = capture.getAllValues().get(1);
        assertJob(job2, "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-{Offset}-2", "child-info-week-{Offset}-2",
                startJobDate.toDate(), jobEndDate.toDate());
    }

    private void assertJob(RepeatingSchedulableJob repeatingSchedulableJob, String jobId, String messageKey, Date jobStartDate, Date jobEndDate) {
        assertDate(jobStartDate, repeatingSchedulableJob.getStartTime());
        assertDate(jobEndDate, repeatingSchedulableJob.getEndTime());
        assertEquals(RepeatingProgramScheduler.INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, repeatingSchedulableJob.getMotechEvent().getSubject());
        assertMotechEvent(repeatingSchedulableJob, jobId, messageKey);
    }

    private void assertDate(Date expectedDate, Date actualDate) {
        DateTime expectedDateTime = new DateTime(expectedDate);
        DateTime actualDateTime = new DateTime(actualDate);
        assertEquals(expectedDateTime.getYear(), actualDateTime.getYear());
        assertEquals(expectedDateTime.getMonthOfYear(), actualDateTime.getMonthOfYear());
        assertEquals(expectedDateTime.getDayOfMonth(), actualDateTime.getDayOfMonth());
    }

    private void assertMotechEvent(RepeatingSchedulableJob repeatingSchedulableJob, String expectedJobId, Object messageKey) {
        assertEquals(expectedJobId, repeatingSchedulableJob.getMotechEvent().getParameters().get("JobID"));
        assertEquals("testCampaign", repeatingSchedulableJob.getMotechEvent().getParameters().get("CampaignName"));
        assertEquals("12345", repeatingSchedulableJob.getMotechEvent().getParameters().get("ExternalID"));
        assertEquals(messageKey, repeatingSchedulableJob.getMotechEvent().getParameters().get("MessageKey"));
    }
}
