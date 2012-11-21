package org.motechproject.server.messagecampaign.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.commons.date.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.EnrollRequestBuilder;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.server.messagecampaign.scheduler.CampaignSchedulerFactory;
import org.motechproject.server.messagecampaign.scheduler.CampaignSchedulerService;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.commons.date.util.DateUtil.now;

public class MessageCampaignServiceImplTest {
    private MessageCampaignServiceImpl messageCampaignService;
    @Mock
    private AllMessageCampaigns allMessageCampaigns;
    @Mock
    private CampaignEnrollmentService campaignEnrollmentService;
    @Mock
    private MotechSchedulerService schedulerService;
    @Mock
    private CampaignEnrollmentRecordMapper campaignEnrollmentRecordMapper;
    @Mock
    private MotechSchedulerService mockSchedulerService;
    @Mock
    private AllCampaignEnrollments allCampaignEnrollments;
    @Mock
    private CampaignSchedulerFactory campaignSchedulerFactory;

    @Before
    public void setUp() {
        initMocks(this);
        messageCampaignService = new MessageCampaignServiceImpl(campaignEnrollmentService, campaignEnrollmentRecordMapper, allCampaignEnrollments, campaignSchedulerFactory);
    }

    @Test
    public void shouldCreateEnrollmentWhenScheduleIsStarted() {
        Campaign campaign = mock(Campaign.class);
        when(allMessageCampaigns.get("testCampaign")).thenReturn(campaign);

        CampaignSchedulerService campaignScheduler = mock(CampaignSchedulerService.class);
        when(campaignSchedulerFactory.getCampaignScheduler("testCampaign")).thenReturn(campaignScheduler);

        CampaignRequest request = new EnrollRequestBuilder().withDefaults()
            .withReferenceDate(new LocalDate(2011, 11, 22))
            .withDeliverTime(new Time(8, 30))
            .build();
        messageCampaignService.startFor(request);

        ArgumentCaptor<CampaignEnrollment> campaignEnrollmentCaptor = ArgumentCaptor.forClass(CampaignEnrollment.class);
        verify(campaignEnrollmentService).register(campaignEnrollmentCaptor.capture());

        CampaignEnrollment campaignEnrollment = campaignEnrollmentCaptor.getValue();
        assertThat(campaignEnrollment.getReferenceDate(), is(new LocalDate(2011, 11, 22)));
    }

    @Test
    public void shouldUnRegisterEnrollmentWhenScheduleIsStopped() {
        Campaign campaign = mock(Campaign.class);
        when(allMessageCampaigns.get("testCampaign")).thenReturn(campaign);

        CampaignSchedulerService campaignScheduler = mock(CampaignSchedulerService.class);
        when(campaignSchedulerFactory.getCampaignScheduler("testCampaign")).thenReturn(campaignScheduler);

        CampaignRequest request = new EnrollRequestBuilder().withDefaults()
            .withReferenceDate(new LocalDate(2011, 11, 22))
            .withDeliverTime(new Time(8, 30))
            .build();
        messageCampaignService.stopAll(request);

        verify(campaignEnrollmentService).unregister(request.externalId(), request.campaignName());
    }

    @Test
    public void shouldCallCampaignSchedulerToStart() {
        CampaignRequest campaignRequest = new CampaignRequest("entity_1", "campaign-name", null, null);

        AbsoluteCampaign absoluteCampaign = mock(AbsoluteCampaign.class);
        when(allMessageCampaigns.get("campaign-name")).thenReturn(absoluteCampaign);

        CampaignSchedulerService campaignScheduler = mock(CampaignSchedulerService.class);
        when(campaignSchedulerFactory.getCampaignScheduler("campaign-name")).thenReturn(campaignScheduler);

        messageCampaignService.startFor(campaignRequest);

        ArgumentCaptor<CampaignEnrollment> enrollment = ArgumentCaptor.forClass(CampaignEnrollment.class);
        verify(campaignScheduler).start(enrollment.capture());
        assertEquals("entity_1", enrollment.getValue().getExternalId());
        assertEquals("campaign-name", enrollment.getValue().getCampaignName());
    }

    @Test
    public void shouldReturnListOfCampaignEnrollmentsForTheGivenQuery() {
        CampaignEnrollmentsQuery enrollmentQuery = mock(CampaignEnrollmentsQuery.class);
        CampaignEnrollment enrollment1 = new CampaignEnrollment("external_id_1", null);
        CampaignEnrollment enrollment2 = new CampaignEnrollment("external_id_2", null);
        List<CampaignEnrollment> enrollments = asList(enrollment1, enrollment2);

        when(campaignEnrollmentService.search(enrollmentQuery)).thenReturn(enrollments);
        CampaignEnrollmentRecord record1 = new CampaignEnrollmentRecord(null, null, null, null);
        CampaignEnrollmentRecord record2 = new CampaignEnrollmentRecord(null, null, null, null);
        when(campaignEnrollmentRecordMapper.map(enrollment1)).thenReturn(record1);
        when(campaignEnrollmentRecordMapper.map(enrollment2)).thenReturn(record2);

        assertEquals(asList(new CampaignEnrollmentRecord[]{record1, record2}), messageCampaignService.search(enrollmentQuery));
    }

    @Test
    public void shouldGetCampaignTimings() {
        AbsoluteCampaign campaign = mock(AbsoluteCampaign.class);

        when(allMessageCampaigns.get("campaign")).thenReturn(campaign);

        CampaignSchedulerService campaignScheduler = mock(CampaignSchedulerService.class);
        when(campaignSchedulerFactory.getCampaignScheduler("campaign")).thenReturn(campaignScheduler);

        CampaignEnrollment enrollment = new CampaignEnrollment("entity_1", "campaign");
        when(allCampaignEnrollments.findByExternalIdAndCampaignName("entity_1", "campaign")).thenReturn(enrollment);

        DateTime now = now();
        DateTime startDate = now.plusDays(1);
        DateTime endDate = now.plusDays(1).plusDays(5);
        messageCampaignService.getCampaignTimings("entity_1", "campaign", startDate, endDate);

        verify(campaignScheduler).getCampaignTimings(startDate, endDate, enrollment);
    }

    @Test
    public void shouldGetEmptyCampaignTimingsMapIfEnrollmentIsNotActive() {
        AbsoluteCampaign campaign = mock(AbsoluteCampaign.class);

        when(allMessageCampaigns.get("campaign")).thenReturn(campaign);

        CampaignSchedulerService campaignScheduler = mock(CampaignSchedulerService.class);
        when(campaignSchedulerFactory.getCampaignScheduler("campaign")).thenReturn(campaignScheduler);

        CampaignEnrollment enrollment = new CampaignEnrollment("entity_1", "campaign");
        enrollment.setStatus(CampaignEnrollmentStatus.INACTIVE);
        when(allCampaignEnrollments.findByExternalIdAndCampaignName("entity_1", "campaign")).thenReturn(enrollment);

        DateTime now = now();
        DateTime startDate = now.plusDays(1);
        DateTime endDate = now.plusDays(1).plusDays(5);
        final Map<String,List<DateTime>> campaignTimings = messageCampaignService.getCampaignTimings("entity_1", "campaign", startDate, endDate);
        assertEquals(0, campaignTimings.size());
    }
}
