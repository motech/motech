package org.motechproject.server.messagecampaign.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.commons.date.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.server.messagecampaign.builder.CampaignBuilder;
import org.motechproject.server.messagecampaign.builder.EnrollRequestBuilder;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.commons.date.util.DateUtil;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.commons.date.util.DateUtil.today;

public class OffsetProgramSchedulerTest {

    public static final String MESSAGE_CAMPAIGN_EVENT_SUBJECT = "org.motechproject.server.messagecampaign.fired-campaign-message";
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
    public void shouldScheduleJobsAfterGivenTimeOffsetIntervalFromReferenceDate_WhenCampaignStartOffsetIsZero() {
        CampaignRequest request = new EnrollRequestBuilder().withDefaults().withReferenceDate(today()).build();
        OffsetCampaign campaign = new CampaignBuilder().defaultOffsetCampaign();

        OffsetCampaignSchedulerService offsetCampaignScheduler = new OffsetCampaignSchedulerService(schedulerService, allMessageCampaigns);

        when(allMessageCampaigns.get("testCampaign")).thenReturn(campaign);

        CampaignEnrollment enrollment = new CampaignEnrollment("12345", "testCampaign").setReferenceDate(today()).setDeliverTime(new Time(9, 30));
        offsetCampaignScheduler.start(enrollment);
        ArgumentCaptor<RunOnceSchedulableJob> capture = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(schedulerService, times(2)).scheduleRunOnceJob(capture.capture());

        List<RunOnceSchedulableJob> allJobs = capture.getAllValues();

        Date startDate1 = DateUtil.newDateTime(DateUtil.today().plusDays(7), request.deliverTime().getHour(), request.deliverTime().getMinute(), 0).toDate();
        assertEquals(startDate1.toString(), allJobs.get(0).getStartDate().toString());
        assertEquals(MESSAGE_CAMPAIGN_EVENT_SUBJECT, allJobs.get(0).getMotechEvent().getSubject());
        assertMotechEvent(allJobs.get(0), "MessageJob.testCampaign.12345.child-info-week-1", "child-info-week-1");

        Date startDate2 = DateUtil.newDateTime(DateUtil.today().plusDays(14), request.deliverTime().getHour(), request.deliverTime().getMinute(), 0).toDate();
        assertEquals(startDate2.toString(), allJobs.get(1).getStartDate().toString());
        assertEquals(MESSAGE_CAMPAIGN_EVENT_SUBJECT, allJobs.get(1).getMotechEvent().getSubject());
        assertMotechEvent(allJobs.get(1), "MessageJob.testCampaign.12345.child-info-week-1a", "child-info-week-1a");
    }

    private void assertMotechEvent(RunOnceSchedulableJob runOnceSchedulableJob, String expectedJobId, String messageKey) {
        assertEquals(expectedJobId, runOnceSchedulableJob.getMotechEvent().getParameters().get("JobID"));
        assertEquals("testCampaign", runOnceSchedulableJob.getMotechEvent().getParameters().get("CampaignName"));
        assertEquals("12345", runOnceSchedulableJob.getMotechEvent().getParameters().get("ExternalID"));
        assertEquals(messageKey, runOnceSchedulableJob.getMotechEvent().getParameters().get("MessageKey"));
    }
}
