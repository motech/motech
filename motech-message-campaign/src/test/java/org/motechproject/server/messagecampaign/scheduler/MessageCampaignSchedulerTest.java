package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.builder.EnrollRequestBuilder;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.today;
import static org.springframework.test.util.ReflectionTestUtils.getField;

class StubCampaignMessage extends CampaignMessage {
}

class StubCampaign extends Campaign<StubCampaignMessage> {


    @Override
    public void setMessages(List<StubCampaignMessage> messages) {
    }

    @Override
    public List<StubCampaignMessage> messages() {
        return new ArrayList<StubCampaignMessage>();
    }

    @Override
    public MessageCampaignScheduler getScheduler(MotechSchedulerService motechSchedulerService, CampaignEnrollmentService campaignEnrollmentService, CampaignRequest enrollRequest) {
        return null;
    }
}

class StubMessageCampaignScheduler extends MessageCampaignScheduler<StubCampaignMessage, StubCampaign> {

    protected StubMessageCampaignScheduler(MotechSchedulerService schedulerService, CampaignRequest campaignRequest, StubCampaign campaign, CampaignEnrollmentService campaignEnrollmentService) {
        super(schedulerService, campaignRequest, campaign, campaignEnrollmentService);
    }

    @Override
    protected void scheduleJobFor(StubCampaignMessage message) {
    }

    @Override
    protected DateTime getCampaignEnd() {
        return newDateTime(today().plusDays(7));
    }
}

public class MessageCampaignSchedulerTest {

    @Mock
    private MotechSchedulerService mockSchedulerService;
    @Mock
    private CampaignEnrollmentService mockCampaignEnrollmentService;


    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldCreateEnrollmentWhenScheduleIsStarted() {
        CampaignRequest request = new EnrollRequestBuilder().withDefaults()
                .withReferenceDate(new LocalDate(2011, 11, 22))
                .withReminderTime(new Time(8, 30))
                .withStartOffset(1)
                .build();
        StubCampaign campaign = new StubCampaign();
        MessageCampaignScheduler messageCampaignScheduler = new StubMessageCampaignScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService);

        messageCampaignScheduler.start();

        ArgumentCaptor<CampaignEnrollment> campaignEnrollmentCaptor = ArgumentCaptor.forClass(CampaignEnrollment.class);
        verify(mockCampaignEnrollmentService).register(campaignEnrollmentCaptor.capture());

        CampaignEnrollment campaignEnrollment = campaignEnrollmentCaptor.getValue();
        assertThat(campaignEnrollment.getStartDate(), is(new LocalDate(2011, 11, 22)));
        assertThat((Integer) getField(campaignEnrollment, "startOffset"), is(1));
    }

    @Test
    public void shouldUnRegisterEnrollmentWhenScheduleIsStopped() {
        CampaignRequest request = new EnrollRequestBuilder().withDefaults()
                .withReferenceDate(new LocalDate(2011, 11, 28))
                .withStartOffset(1)
                .build();
        StubCampaign campaign = new StubCampaign();
        MessageCampaignScheduler messageCampaignScheduler = new StubMessageCampaignScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService);

        messageCampaignScheduler.stop();

        verify(mockCampaignEnrollmentService).unregister(request.externalId(), request.campaignName());
    }

    @Test
    public void shouldScheduleJobToCaptureEndOfCampaign() {
        CampaignRequest request = new EnrollRequestBuilder().withDefaults()
                .withExternalId("entity_1")
                .withReferenceDate(new LocalDate(2011, 11, 28))
                .withStartOffset(1)
                .build();
        StubCampaign campaign = new StubCampaign();
        campaign.setName("campaign_name");
      
        MessageCampaignScheduler messageCampaignScheduler = new StubMessageCampaignScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService);

        messageCampaignScheduler.start();

        ArgumentCaptor<RunOnceSchedulableJob> jobCaptor = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(mockSchedulerService).safeScheduleRunOnceJob(jobCaptor.capture());

        RunOnceSchedulableJob job = jobCaptor.getValue();
        assertEquals(newDateTime(today().plusDays(7)).toDate(), job.getStartDate());
        assertEquals(EventKeys.MESSAGE_CAMPAIGN_COMPLETED_EVENT_SUBJECT, job.getMotechEvent().getSubject());
        assertEquals("CampaignCompletedJob.campaign_name.entity_1",job.getMotechEvent().getParameters().get(EventKeys.SCHEDULE_JOB_ID_KEY));
        CampaignEnrollment enrollment = (CampaignEnrollment) job.getMotechEvent().getParameters().get(EventKeys.ENROLLMENT_KEY);
        assertEquals("entity_1", enrollment.getExternalId());
    }

    @Test
    public void shouldUnscheduleCompletionJobOnStoppingCampaign() {
        CampaignRequest request = new EnrollRequestBuilder().withDefaults()
                .withReferenceDate(new LocalDate(2011, 11, 28))
                .withExternalId("foo")
                .withStartOffset(1)
                .build();
        StubCampaign campaign = new StubCampaign();
        campaign.setName("campaign_name");
        MessageCampaignScheduler messageCampaignScheduler = new StubMessageCampaignScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService);

        messageCampaignScheduler.stop();

        verify(mockSchedulerService).safeUnscheduleJob(EventKeys.MESSAGE_CAMPAIGN_COMPLETED_EVENT_SUBJECT, "CampaignCompletedJob.campaign_name.foo");

    }
}
