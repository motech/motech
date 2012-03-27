package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.CampaignBuilder;
import org.motechproject.server.messagecampaign.builder.EnrollRequestBuilder;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatingCampaign;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.getField;

public class MessageCampaignSchedulerTest{

    @Mock
    private MotechSchedulerService mockSchedulerService;
    @Mock
    private CampaignEnrollmentService mockCampaignEnrollmentService;


    @Before
    public void setUp()
    {
        initMocks(this);
    }

    private void assertCampaignEnrollment(Integer startOffset, LocalDate startDate, CampaignEnrollment campaignEnrollment) {
        assertThat(campaignEnrollment.getStartDate(), is(startDate));
        assertThat((Integer) getField(campaignEnrollment, "startOffset"), is(startOffset));
    }


    @Test
    public void shouldCreateEnrollmentWhenScheduleIsStarted() {
        LocalDate startDate = new LocalDate(2011, 11, 22);
        Integer startOffset = 1;
        Time reminderTime = new Time(8, 30);
        CampaignRequest request = new EnrollRequestBuilder().withDefaults().withReferenceDate(startDate).withReminderTime(reminderTime).withStartOffset(startOffset).build();
        RepeatingCampaign campaign = new CampaignBuilder().repeatingCampaign("campaignName", "2 Weeks",new ArrayList<RepeatingCampaignMessage>());
        MessageCampaignScheduler messageProgramScheduler = new RepeatingProgramScheduler(mockSchedulerService,request,campaign,mockCampaignEnrollmentService,false);

        messageProgramScheduler.start();

        ArgumentCaptor<CampaignEnrollment> campaignEnrollmentCaptor = ArgumentCaptor.forClass(CampaignEnrollment.class);
        verify(mockCampaignEnrollmentService, times(1)).register(campaignEnrollmentCaptor.capture());
        assertCampaignEnrollment(startOffset, startDate, campaignEnrollmentCaptor.getValue());
    }

    @Test
    public void shouldUnRegisterEnrollmentWhenScheduleIsStopped(){
        RepeatingCampaign campaign = new CampaignBuilder().defaultRepeatingCampaign("2 Weeks");
        CampaignRequest request = new EnrollRequestBuilder().withDefaults().withReferenceDate(new LocalDate(2011, 11, 28)).withStartOffset(1).build();
        RepeatingProgramScheduler repeatingProgramScheduler = new RepeatingProgramScheduler(mockSchedulerService, request, campaign, mockCampaignEnrollmentService, false);
        repeatingProgramScheduler.stop();
        verify(mockCampaignEnrollmentService).unregister(request.externalId(), request.campaignName());
    }
}
