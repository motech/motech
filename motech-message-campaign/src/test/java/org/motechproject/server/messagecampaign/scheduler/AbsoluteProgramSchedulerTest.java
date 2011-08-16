package org.motechproject.server.messagecampaign.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.CampaignBuilder;
import org.motechproject.server.messagecampaign.builder.EnrollRequestBuilder;
import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.motechproject.server.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.util.DateUtil;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AbsoluteProgramSchedulerTest {

    private MotechSchedulerService schedulerService;

    @Before
    public void setUp() {
        schedulerService = mock(MotechSchedulerService.class);
        initMocks(this);
    }

    @Test
    public void shouldScheduleJobs() {
        EnrollRequest request = new EnrollRequestBuilder().withDefaults().build();
        AbsoluteCampaign campaign = new CampaignBuilder().defaultAbsoluteCampaign();

        AbsoluteProgramScheduler absoluteProgramScheduler = new AbsoluteProgramScheduler(schedulerService, request, campaign);

        absoluteProgramScheduler.scheduleJobs();
        ArgumentCaptor<CronSchedulableJob> capture = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(schedulerService, times(2)).scheduleJob(capture.capture());

        List<CronSchedulableJob> allJobs = capture.getAllValues();
        assertEquals(campaign.messages().get(0).date(), DateUtil.newDate(allJobs.get(0).getStartTime()));
        assertEquals("0 30/15 9-11 * * ?", allJobs.get(0).getCronExpression());
        assertEquals("org.motechproject.server.messagecampaign.created-campaign-message", allJobs.get(0).getMotechEvent().getSubject());
        assertMotechEvent(allJobs.get(0), "org.motechproject.server.messagecampaign.testCampaign.12345.random-1", "random-1");

        assertEquals(campaign.messages().get(1).date(), DateUtil.newDate(allJobs.get(1).getStartTime()));
        assertEquals("0 30/15 9-11 * * ?", allJobs.get(1).getCronExpression());
        assertEquals("org.motechproject.server.messagecampaign.created-campaign-message", allJobs.get(1).getMotechEvent().getSubject());
        assertMotechEvent(allJobs.get(1), "org.motechproject.server.messagecampaign.testCampaign.12345.random-2", "random-2");
    }

    private void assertMotechEvent(CronSchedulableJob cronSchedulableJob, String expectedJobId, Object messageKey) {
        assertEquals(expectedJobId, cronSchedulableJob.getMotechEvent().getParameters().get("JobID"));
        assertEquals("testCampaign", cronSchedulableJob.getMotechEvent().getParameters().get("CampaignName"));
        assertEquals("12345", cronSchedulableJob.getMotechEvent().getParameters().get("ExternalID"));
        assertEquals(messageKey, cronSchedulableJob.getMotechEvent().getParameters().get("MessageKey"));
    }
}
