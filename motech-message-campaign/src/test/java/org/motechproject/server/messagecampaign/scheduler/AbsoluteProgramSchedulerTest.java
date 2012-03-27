package org.motechproject.server.messagecampaign.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.CampaignBuilder;
import org.motechproject.server.messagecampaign.builder.EnrollRequestBuilder;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.newDateTime;

public class AbsoluteProgramSchedulerTest {

    private MotechSchedulerService schedulerService;

    @Mock
    private CampaignEnrollmentService mockCampaignEnrollmentService;


    @Before
    public void setUp() {
        schedulerService = mock(MotechSchedulerService.class);
        initMocks(this);
    }

    @Test
    public void shouldScheduleJobs() {
        CampaignRequest request = new EnrollRequestBuilder().withDefaults().build();
        AbsoluteCampaign campaign = new CampaignBuilder().defaultAbsoluteCampaign();

        AbsoluteProgramScheduler absoluteProgramScheduler = new AbsoluteProgramScheduler(schedulerService, request, campaign, mockCampaignEnrollmentService);

        absoluteProgramScheduler.start();
        ArgumentCaptor<RunOnceSchedulableJob> capture = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(schedulerService, times(2)).scheduleRunOnceJob(capture.capture());

        List<RunOnceSchedulableJob> allJobs = capture.getAllValues();

        Date startDate1 = DateUtil.newDateTime(campaign.messages().get(0).date(), request.reminderTime().getHour(), request.reminderTime().getMinute(), 0).toDate();
        assertEquals(startDate1.toString(), allJobs.get(0).getStartDate().toString());
        assertEquals("org.motechproject.server.messagecampaign.send-campaign-message", allJobs.get(0).getMotechEvent().getSubject());
        assertMotechEvent(allJobs.get(0), "testCampaign.12345.random-1", "random-1");

        Date startDate2 = DateUtil.newDateTime(campaign.messages().get(1).date(), request.reminderTime().getHour(), request.reminderTime().getMinute(), 0).toDate();
        assertEquals(startDate2.toString(), allJobs.get(1).getStartDate().toString());
        assertEquals("org.motechproject.server.messagecampaign.send-campaign-message", allJobs.get(1).getMotechEvent().getSubject());
        assertMotechEvent(allJobs.get(1), "testCampaign.12345.random-2", "random-2");
    }

    @Test
    public void shouldGetDurationOfSchedule() {
        CampaignRequest request = new EnrollRequestBuilder().withDefaults().build();
        AbsoluteCampaign campaign = new CampaignBuilder().defaultAbsoluteCampaign();
        AbsoluteProgramScheduler absoluteProgramScheduler = new AbsoluteProgramScheduler(schedulerService, request, campaign, mockCampaignEnrollmentService);

        assertEquals(newDateTime(DateUtil.today().plusDays(2), request.reminderTime()), absoluteProgramScheduler.getCampaignEnd());
    }

    private void assertMotechEvent(RunOnceSchedulableJob runOnceSchedulableJob, String expectedJobId, Object messageKey) {
        assertEquals(expectedJobId, runOnceSchedulableJob.getMotechEvent().getParameters().get("JobID"));
        assertEquals("testCampaign", runOnceSchedulableJob.getMotechEvent().getParameters().get("CampaignName"));
        assertEquals("12345", runOnceSchedulableJob.getMotechEvent().getParameters().get("ExternalID"));
        assertEquals(messageKey, runOnceSchedulableJob.getMotechEvent().getParameters().get("MessageKey"));
    }
}
