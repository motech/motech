package org.motechproject.server.messagecampaign.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.CampaignBuilder;
import org.motechproject.server.messagecampaign.builder.EnrollRequestBuilder;
import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OffsetProgramSchedulerTest {

    public static final String MESSAGE_CAMPAIGN_EVENT_SUBJECT = "org.motechproject.server.messagecampaign.scheduler-message";
    private MotechSchedulerService schedulerService;

    @Before
    public void setUp() {
        schedulerService = mock(MotechSchedulerService.class);
        initMocks(this);
    }

    @Test
    public void shouldScheduleJobs() {
        EnrollRequest request = new EnrollRequestBuilder().withDefaults().build();
        OffsetCampaign campaign = new CampaignBuilder().defaultOffsetCampaign();

        OffsetProgramScheduler offsetProgramScheduler = new OffsetProgramScheduler(schedulerService, request, campaign);

        offsetProgramScheduler.scheduleJobs();
        ArgumentCaptor<CronSchedulableJob> capture = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(schedulerService, times(2)).scheduleJob(capture.capture());

        List<CronSchedulableJob> allJobs = capture.getAllValues();

        Date jobDate1 = request.referenceDate().plusDays(7).toDate();
        assertEquals("0 30/15 9-11 * * ?", allJobs.get(0).getCronExpression());
        jobDate1.setSeconds(0); allJobs.get(0).getStartTime().setSeconds(0);
        assertEquals(jobDate1.toString(), allJobs.get(0).getStartTime().toString());
        assertEquals(MESSAGE_CAMPAIGN_EVENT_SUBJECT, allJobs.get(0).getMotechEvent().getSubject());
        assertMotechEvent(allJobs.get(0), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-1", "child-info-week-1");

        Date jobDate2 = request.referenceDate().plusDays(14).toDate();
        assertEquals("0 30/15 9-11 * * ?", allJobs.get(1).getCronExpression());
        jobDate2.setSeconds(0); allJobs.get(1).getStartTime().setSeconds(0);
        assertEquals(jobDate2.toString(), allJobs.get(1).getStartTime().toString());
        assertEquals(MESSAGE_CAMPAIGN_EVENT_SUBJECT, allJobs.get(1).getMotechEvent().getSubject());
        assertMotechEvent(allJobs.get(1), "org.motechproject.server.messagecampaign.testCampaign.12345.child-info-week-1a", "child-info-week-1a");
    }

    private void assertMotechEvent(CronSchedulableJob cronSchedulableJob, String expectedJobId, String messageKey) {
        assertEquals(expectedJobId, cronSchedulableJob.getMotechEvent().getParameters().get("JobID"));
        assertEquals("testCampaign", cronSchedulableJob.getMotechEvent().getParameters().get("CampaignName"));
        assertEquals("12345", cronSchedulableJob.getMotechEvent().getParameters().get("ExternalID"));
        assertEquals(messageKey, cronSchedulableJob.getMotechEvent().getParameters().get("MessageKey"));
    }
}
