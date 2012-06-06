package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.CampaignBuilder;
import org.motechproject.server.messagecampaign.builder.CampaignMessageBuilder;
import org.motechproject.server.messagecampaign.builder.EnrollRequestBuilder;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.*;

public class OffsetProgramSchedulerTest {

    public static final String MESSAGE_CAMPAIGN_EVENT_SUBJECT = "org.motechproject.server.messagecampaign.send-campaign-message";
    private MotechSchedulerService schedulerService;
    @Mock
    private CampaignEnrollmentService mockCampaignEnrollmentService;

    @Before
    public void setUp() {
        schedulerService = mock(MotechSchedulerService.class);
        initMocks(this);
    }

    @Test
    public void sshouldScheduleJobsAfterGivenTimeOffsetIntervalFromReferenceDate_WhenCampaignStartOffsetIsZero() {
        CampaignRequest request = new EnrollRequestBuilder().withDefaults().withReferenceDate(today()).build();
        OffsetCampaign campaign = new CampaignBuilder().defaultOffsetCampaign();

        OffsetProgramScheduler offsetProgramScheduler = new OffsetProgramScheduler(schedulerService, request, campaign, mockCampaignEnrollmentService);

        offsetProgramScheduler.start();
        ArgumentCaptor<RunOnceSchedulableJob> capture = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(schedulerService, times(2)).scheduleRunOnceJob(capture.capture());

        List<RunOnceSchedulableJob> allJobs = capture.getAllValues();

        Date startDate1 = DateUtil.newDateTime(DateUtil.today().plusDays(7), request.reminderTime().getHour(), request.reminderTime().getMinute(), 0).toDate();
        assertEquals(startDate1.toString(), allJobs.get(0).getStartDate().toString());
        assertEquals(MESSAGE_CAMPAIGN_EVENT_SUBJECT, allJobs.get(0).getMotechEvent().getSubject());
        assertMotechEvent(allJobs.get(0), "MessageJob.testCampaign.12345.child-info-week-1", "child-info-week-1");

        Date startDate2 = DateUtil.newDateTime(DateUtil.today().plusDays(14), request.reminderTime().getHour(), request.reminderTime().getMinute(), 0).toDate();
        assertEquals(startDate2.toString(), allJobs.get(1).getStartDate().toString());
        assertEquals(MESSAGE_CAMPAIGN_EVENT_SUBJECT, allJobs.get(1).getMotechEvent().getSubject());
        assertMotechEvent(allJobs.get(1), "MessageJob.testCampaign.12345.child-info-week-1a", "child-info-week-1a");
    }

    @Test
    public void sdhouldScheduleJobsTakingIntoAccountCampaignStartOffset_WhenCampaignStartOffsetIsNotZero() {
        CampaignRequest request = new EnrollRequestBuilder().withDefaults().withReferenceDate(today()).withStartOffset(10).build();
        OffsetCampaign campaign = new CampaignBuilder().defaultOffsetCampaign();
        List<OffsetCampaignMessage> campaignMessages = new ArrayList<OffsetCampaignMessage>();
        campaignMessages.add(new CampaignMessageBuilder().offsetCampaignMessage("OM1", "1 Week", "child-info-week-1"));
        campaignMessages.add(new CampaignMessageBuilder().offsetCampaignMessage("OM2", "2 weeks", "child-info-week-2"));
        campaign.setMessages(campaignMessages);
        OffsetProgramScheduler offsetProgramScheduler = new OffsetProgramScheduler(schedulerService, request, campaign, mockCampaignEnrollmentService);

        offsetProgramScheduler.start();
        ArgumentCaptor<RunOnceSchedulableJob> capture = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(schedulerService, times(1)).scheduleRunOnceJob(capture.capture());

        RunOnceSchedulableJob scheduledJob = capture.getAllValues().get(0);

        LocalDate expectedStartDate = DateUtil.today().plusDays(14).minusDays(10); // 2 weeks with offset of 10 days
        Date startDate = DateUtil.newDateTime(expectedStartDate, request.reminderTime().getHour(), request.reminderTime().getMinute(), 0).toDate();
        assertEquals(startDate.toString(), scheduledJob.getStartDate().toString());
        assertEquals(MESSAGE_CAMPAIGN_EVENT_SUBJECT, scheduledJob.getMotechEvent().getSubject());
        assertMotechEvent(scheduledJob, "MessageJob.testCampaign.12345.child-info-week-2", "child-info-week-2");
    }

    @Test
    public void shouldReturnCampaignEndTime() {
        CampaignRequest request = new EnrollRequestBuilder().withDefaults().withReferenceDate(newDate(2011, 5, 5)).build();
        OffsetCampaign campaign = new CampaignBuilder().defaultOffsetCampaign();
        OffsetProgramScheduler offsetProgramScheduler = new OffsetProgramScheduler(schedulerService, request, campaign, mockCampaignEnrollmentService);

        assertEquals(newDateTime(2011, 5, 5, 9, 30, 0).plusWeeks(2), offsetProgramScheduler.getCampaignEnd());
    }

    private void assertMotechEvent(RunOnceSchedulableJob runOnceSchedulableJob, String expectedJobId, String messageKey) {
        assertEquals(expectedJobId, runOnceSchedulableJob.getMotechEvent().getParameters().get("JobID"));
        assertEquals("testCampaign", runOnceSchedulableJob.getMotechEvent().getParameters().get("CampaignName"));
        assertEquals("12345", runOnceSchedulableJob.getMotechEvent().getParameters().get("ExternalID"));
        assertEquals(messageKey, runOnceSchedulableJob.getMotechEvent().getParameters().get("MessageKey"));
    }
}
