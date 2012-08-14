package org.motechproject.server.messagecampaign.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.server.messagecampaign.builder.CampaignBuilder;
import org.motechproject.server.messagecampaign.builder.EnrollRequestBuilder;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.CronBasedCampaign;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.util.DateUtil;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.today;

public class CronBasedProgramSchedulerTest {

    private MotechSchedulerService schedulerService;
    @Mock
    private CampaignEnrollmentService mockCampaignEnrollmentService;
    @Mock
    private AllMessageCampaigns allMessageCampaigns;

    @Before
    public void setUp() {
        schedulerService = mock(MotechSchedulerService.class);
        initMocks(this);
    }

    @Test
    public void shouldScheduleJobs() {
        CampaignRequest request = new EnrollRequestBuilder().withDefaults().withReferenceDate(today()).build();
        CronBasedCampaign campaign = new CampaignBuilder().defaultCronBasedCampaign();

        CronBasedCampaignSchedulerService cronBasedCampaignScheduler = new CronBasedCampaignSchedulerService(schedulerService, allMessageCampaigns);

        when(allMessageCampaigns.get("testCampaign")).thenReturn(campaign);

        CampaignEnrollment enrollment = new CampaignEnrollment("12345", "testCampaign").setReferenceDate(today()).setDeliverTime(new Time(9, 30));
        cronBasedCampaignScheduler.start(enrollment);
        ArgumentCaptor<CronSchedulableJob> capture = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(schedulerService, times(2)).scheduleJob(capture.capture());

        List<CronSchedulableJob> allJobs = capture.getAllValues();
        assertEquals(campaign.getMessages().get(0).cron(), allJobs.get(0).getCronExpression());
        assertEquals(DateUtil.today(), DateUtil.newDate(allJobs.get(0).getStartTime()));
        assertEquals("org.motechproject.server.messagecampaign.fired-campaign-message", allJobs.get(0).getMotechEvent().getSubject());
        assertMotechEvent(allJobs.get(0), "MessageJob.testCampaign.12345.cron-message1", "cron-message1");

        assertEquals(campaign.getMessages().get(1).cron(), allJobs.get(1).getCronExpression());
        assertEquals(DateUtil.today(), DateUtil.newDate(allJobs.get(1).getStartTime()));
        assertEquals("org.motechproject.server.messagecampaign.fired-campaign-message", allJobs.get(1).getMotechEvent().getSubject());
        assertMotechEvent(allJobs.get(1), "MessageJob.testCampaign.12345.cron-message2", "cron-message2");
    }

    private void assertMotechEvent(CronSchedulableJob cronSchedulableJob, String expectedJobId, String messageKey) {
        assertEquals(expectedJobId, cronSchedulableJob.getMotechEvent().getParameters().get("JobID"));
        assertEquals("testCampaign", cronSchedulableJob.getMotechEvent().getParameters().get("CampaignName"));
        assertEquals("12345", cronSchedulableJob.getMotechEvent().getParameters().get("ExternalID"));
        assertEquals(messageKey, cronSchedulableJob.getMotechEvent().getParameters().get("MessageKey"));
    }
}
