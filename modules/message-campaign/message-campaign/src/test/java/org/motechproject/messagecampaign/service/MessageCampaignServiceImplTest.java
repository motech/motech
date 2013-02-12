package org.motechproject.messagecampaign.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.commons.date.model.Time;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.messagecampaign.EventKeys;
import org.motechproject.messagecampaign.builder.EnrollRequestBuilder;
import org.motechproject.messagecampaign.contract.CampaignRequest;
import org.motechproject.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.messagecampaign.domain.CampaignNotFoundException;
import org.motechproject.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.messagecampaign.domain.campaign.Campaign;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.messagecampaign.scheduler.CampaignSchedulerFactory;
import org.motechproject.messagecampaign.scheduler.CampaignSchedulerService;
import org.motechproject.messagecampaign.search.Criterion;
import org.motechproject.messagecampaign.userspecified.CampaignRecord;
import org.motechproject.messagecampaign.web.ex.EnrollmentNotFoundException;
import org.motechproject.scheduler.MotechSchedulerService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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
    @Mock
    private CampaignRecord campaignRecord;
    @Mock
    private EventRelay eventRelay;

    @Before
    public void setUp() {
        initMocks(this);
        messageCampaignService = new MessageCampaignServiceImpl(campaignEnrollmentService, campaignEnrollmentRecordMapper,
                allCampaignEnrollments, campaignSchedulerFactory, allMessageCampaigns, eventRelay);
    }

    @Test
    public void shouldCreateEnrollmentWhenScheduleIsStarted() {
        Campaign campaign = mock(Campaign.class);
        when(allMessageCampaigns.getCampaign("testCampaign")).thenReturn(campaign);

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

        ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(motechEventArgumentCaptor.capture());
        MotechEvent event = motechEventArgumentCaptor.getValue();
        assertEquals(event.getSubject(), EventKeys.ENROLLED_USER_SUBJECT);
        assertEquals(request.externalId(), event.getParameters().get(EventKeys.EXTERNAL_ID_KEY));
        assertEquals(request.campaignName(), event.getParameters().get(EventKeys.CAMPAIGN_NAME_KEY));
    }

    @Test
    public void shouldUnRegisterEnrollmentWhenScheduleIsStopped() {
        Campaign campaign = mock(Campaign.class);
        when(allMessageCampaigns.getCampaign("testCampaign")).thenReturn(campaign);

        CampaignSchedulerService campaignScheduler = mock(CampaignSchedulerService.class);
        when(campaignSchedulerFactory.getCampaignScheduler("testCampaign")).thenReturn(campaignScheduler);

        CampaignRequest request = new EnrollRequestBuilder().withDefaults()
            .withReferenceDate(new LocalDate(2011, 11, 22))
            .withDeliverTime(new Time(8, 30))
            .build();
        messageCampaignService.stopAll(request);

        verify(campaignEnrollmentService).unregister(request.externalId(), request.campaignName());

        ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(motechEventArgumentCaptor.capture());
        MotechEvent event = motechEventArgumentCaptor.getValue();
        assertEquals(event.getSubject(), EventKeys.UNENROLLED_USER_SUBJECT);
        assertEquals(request.externalId(), event.getParameters().get(EventKeys.EXTERNAL_ID_KEY));
        assertEquals(request.campaignName(), event.getParameters().get(EventKeys.CAMPAIGN_NAME_KEY));
    }

    @Test
    public void shouldUnregisterAllCampaignsMatchingQuery() {
        Campaign campaign = mock(Campaign.class);
        when(allMessageCampaigns.getCampaign("testCampaign")).thenReturn(campaign);

        CampaignEnrollment enrollment1 = new CampaignEnrollment("external_id_1", "testCampaign");
        CampaignEnrollment enrollment2 = new CampaignEnrollment("external_id_2", "testCampaign");
        when(campaignEnrollmentService.search(any(CampaignEnrollmentsQuery.class)))
                .thenReturn(asList(enrollment1, enrollment2));

        CampaignSchedulerService campaignScheduler = mock(CampaignSchedulerService.class);
        when(campaignSchedulerFactory.getCampaignScheduler("testCampaign")).thenReturn(campaignScheduler);

        CampaignEnrollmentsQuery query = new CampaignEnrollmentsQuery().withCampaignName("testCampaign");

        messageCampaignService.stopAll(query);

        ArgumentCaptor<CampaignEnrollmentsQuery> captor = ArgumentCaptor.forClass(CampaignEnrollmentsQuery.class);
        verify(campaignEnrollmentService).search(captor.capture());

        assertEquals(0, captor.getValue().getSecondaryCriteria().size());
        Criterion primaryCriterion = captor.getValue().getPrimaryCriterion();
        primaryCriterion.fetch(allCampaignEnrollments);
        verify(allCampaignEnrollments).findByCampaignName("testCampaign");

        verify(campaignEnrollmentService).unregister(enrollment1.getExternalId(), enrollment2.getCampaignName());
        verify(campaignEnrollmentService).unregister(enrollment2.getExternalId(), enrollment2.getCampaignName());
        verify(campaignSchedulerFactory, times(2)).getCampaignScheduler("testCampaign");
        verify(campaignScheduler).stop(enrollment1);
        verify(campaignScheduler).stop(enrollment2);
    }

    @Test
    public void shouldCallCampaignSchedulerToStart() {
        CampaignRequest campaignRequest = new CampaignRequest("entity_1", "campaign-name", null, null, null);

        AbsoluteCampaign absoluteCampaign = mock(AbsoluteCampaign.class);
        when(allMessageCampaigns.getCampaign("campaign-name")).thenReturn(absoluteCampaign);

        CampaignSchedulerService campaignScheduler = mock(CampaignSchedulerService.class);
        when(campaignSchedulerFactory.getCampaignScheduler("campaign-name")).thenReturn(campaignScheduler);

        messageCampaignService.startFor(campaignRequest);

        ArgumentCaptor<CampaignEnrollment> enrollment = ArgumentCaptor.forClass(CampaignEnrollment.class);
        verify(campaignScheduler).start(enrollment.capture());
        assertEquals("entity_1", enrollment.getValue().getExternalId());
        assertEquals("campaign-name", enrollment.getValue().getCampaignName());

        ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(motechEventArgumentCaptor.capture());
        MotechEvent event = motechEventArgumentCaptor.getValue();
        assertEquals(event.getSubject(), EventKeys.ENROLLED_USER_SUBJECT);
        assertEquals(enrollment.getValue().getExternalId(), event.getParameters().get(EventKeys.EXTERNAL_ID_KEY));
        assertEquals(enrollment.getValue().getCampaignName(), event.getParameters().get(EventKeys.CAMPAIGN_NAME_KEY));
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

        when(allMessageCampaigns.getCampaign("campaign")).thenReturn(campaign);

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

        when(allMessageCampaigns.getCampaign("campaign")).thenReturn(campaign);

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

    @Test
    public void shouldSaveCampaigns() {
        messageCampaignService.saveCampaign(campaignRecord);
        verify(allMessageCampaigns).saveOrUpdate(campaignRecord);
    }

    @Test
    public void shouldRemoveCampaigns() {
        when(allMessageCampaigns.findFirstByName("PREGNANCY")).thenReturn(campaignRecord);
        when(campaignEnrollmentService.search(any(CampaignEnrollmentsQuery.class)))
                .thenReturn(Collections.<CampaignEnrollment>emptyList());

        messageCampaignService.deleteCampaign("PREGNANCY");

        verify(allMessageCampaigns).findFirstByName("PREGNANCY");
        verify(allMessageCampaigns).remove(campaignRecord);

        ArgumentCaptor<CampaignEnrollmentsQuery> captor = ArgumentCaptor.forClass(CampaignEnrollmentsQuery.class);
        verify(campaignEnrollmentService).search(captor.capture());

        Criterion primaryCriterion = captor.getValue().getPrimaryCriterion();
        primaryCriterion.fetch(allCampaignEnrollments);
        verify(allCampaignEnrollments).findByCampaignName("PREGNANCY");
        assertTrue(captor.getValue().getSecondaryCriteria().isEmpty());
    }

    @Test
    public void shouldRetrieveCampaignByName() {
        when(allMessageCampaigns.findFirstByName("PREGNANCY")).thenReturn(campaignRecord);
        assertEquals(campaignRecord, messageCampaignService.getCampaignRecord("PREGNANCY"));
        verify(allMessageCampaigns).findFirstByName("PREGNANCY");
    }

    @Test
    public void shouldRetrieveAllCampaigns() {
        when(allMessageCampaigns.getAll()).thenReturn(asList(campaignRecord));
        assertEquals(asList(campaignRecord), messageCampaignService.getAllCampaignRecords());
        verify(allMessageCampaigns).getAll();
    }

    @Test(expected = CampaignNotFoundException.class)
    public void shouldThrowExceptionWhenDeletingNonExistantCampaign() {
        when(allMessageCampaigns.findFirstByName("PREGNANCY")).thenReturn(null);
        messageCampaignService.deleteCampaign("PREGNANCY");
    }

    @Test
    public void shouldUpdateExistingEnrollments() {
        final LocalDate now = LocalDate.now();

        CampaignEnrollment enrollment = new CampaignEnrollment("oldExtId", "campaign");
        enrollment.setStatus(CampaignEnrollmentStatus.ACTIVE);
        enrollment.setDeliverTime(10, 50);
        enrollment.setReferenceDate(now.plusWeeks(1));
        enrollment.setReferenceTime(10, 51);

        CampaignSchedulerService campaignScheduler = mock(CampaignSchedulerService.class);

        when(allCampaignEnrollments.get("enrollmentId")).thenReturn(enrollment);
        when(campaignSchedulerFactory.getCampaignScheduler("campaign")).thenReturn(campaignScheduler);

        CampaignRequest campaignRequest = new CampaignRequest("extId", "campaign", now, new Time(11, 0), new Time(10, 0));

        messageCampaignService.updateEnrollment(campaignRequest, "enrollmentId");

        verify(campaignScheduler).stop(enrollment);

        ArgumentMatcher<CampaignEnrollment> matcher = new ArgumentMatcher<CampaignEnrollment>() {
            @Override
            public boolean matches(Object argument) {
                CampaignEnrollment enrollment = (CampaignEnrollment) argument;
                return Objects.equals("campaign", enrollment.getCampaignName()) && Objects.equals("extId", enrollment.getExternalId())
                        && Objects.equals(new Time(11, 0), enrollment.getReferenceTime()) && Objects.equals(new Time(10, 0), enrollment.getDeliverTime())
                        && Objects.equals(now, enrollment.getReferenceDate());
            }
        };

        verify(allCampaignEnrollments).saveOrUpdate(argThat(matcher));
        verify(campaignScheduler).start(argThat(matcher));
    }

    @Test(expected = EnrollmentNotFoundException.class)
    public void shouldThrowExceptionWhenUpdatingNonExistentEnrollment() {
        when(allCampaignEnrollments.get("id")).thenReturn(null);
        messageCampaignService.updateEnrollment(new CampaignRequest(), "id");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForDuplicateExtIdAndCampaignName() {
        CampaignEnrollment enrollment = new CampaignEnrollment("extId", "PREGNANCY");
        enrollment.setId("couchId");
        CampaignEnrollment otherEnrollment = new CampaignEnrollment("extId2", "PREGNANCY");
        otherEnrollment.setId("otherEnrollmentId");

        when(allCampaignEnrollments.get("couchId")).thenReturn(enrollment);
        when(allCampaignEnrollments.findByExternalIdAndCampaignName("extId2", "PREGNANCY")).thenReturn(otherEnrollment);

        CampaignRequest campaignRequest = new CampaignRequest("extId2", "PREGNANCY", null, null, null);

        messageCampaignService.updateEnrollment(campaignRequest, "couchId");
    }
}
