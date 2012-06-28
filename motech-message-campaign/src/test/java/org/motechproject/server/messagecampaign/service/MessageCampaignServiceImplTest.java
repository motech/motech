package org.motechproject.server.messagecampaign.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.MessageCampaignException;
import org.motechproject.server.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.scheduler.MessageCampaignScheduler;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessageCampaignServiceImplTest {
    private MessageCampaignServiceImpl messageCampaignService;
    @Mock
    private AllMessageCampaigns allMessageCampaigns;
    @Mock
    private CampaignEnrollmentService mockCampaignEnrollmentService;
    @Mock
    private MotechSchedulerService schedulerService;
    @Mock
    private MessageCampaignScheduler scheduler;
    @Mock
    private CampaignEnrollmentRecordMapper mockCampaignEnrollmentRecordMapper;

    @Before
    public void setUp() {
        initMocks(this);
        messageCampaignService = new MessageCampaignServiceImpl(allMessageCampaigns, schedulerService, mockCampaignEnrollmentService, mockCampaignEnrollmentRecordMapper);
    }

    @Test
    public void shouldCallCampaignSchedulerToStart() {
        String campaignName = "campaign-name";
        CampaignRequest campaignRequest = new CampaignRequest();
        campaignRequest.setCampaignName(campaignName);
        AbsoluteCampaign absoluteCampaign = mock(AbsoluteCampaign.class);

        when(allMessageCampaigns.get(campaignName)).thenReturn(absoluteCampaign);
        when(absoluteCampaign.getScheduler(schedulerService, mockCampaignEnrollmentService, campaignRequest)).thenReturn(scheduler);

        messageCampaignService.startFor(campaignRequest);

        verify(absoluteCampaign).getScheduler(schedulerService, mockCampaignEnrollmentService, campaignRequest);
        verify(scheduler).start();
    }

    @Test
    public void shouldCallCampaignSchedulerToStop() {
         String campaignName = "campaign-name";
        CampaignRequest campaignRequest = new CampaignRequest();
        campaignRequest.setCampaignName(campaignName);
        AbsoluteCampaign absoluteCampaign = mock(AbsoluteCampaign.class);

        when(allMessageCampaigns.get(campaignName)).thenReturn(absoluteCampaign);
        when(absoluteCampaign.getScheduler(schedulerService, mockCampaignEnrollmentService, campaignRequest)).thenReturn(scheduler);

        messageCampaignService.stopFor(campaignRequest, "foo");

        verify(absoluteCampaign).getScheduler(schedulerService, mockCampaignEnrollmentService, campaignRequest);
        verify(scheduler).stop("foo");
    }

    @Test(expected = MessageCampaignException.class)
    public void enrollWithUnknownCampaignTest() {
        String campaignName = "non-existent-campaign-name";
        CampaignRequest enrollRequest = new CampaignRequest();
        enrollRequest.setCampaignName(campaignName);

        when(allMessageCampaigns.get(campaignName)).thenReturn(null);

        messageCampaignService.startFor(enrollRequest);
    }

    @Test
    public void shouldReturnListOfCampaignEnrollmentsForTheGivenQuery() {
        CampaignEnrollmentsQuery enrollmentQuery = mock(CampaignEnrollmentsQuery.class);
        CampaignEnrollment enrollment1 = new CampaignEnrollment("external_id_1", null);
        CampaignEnrollment enrollment2 = new CampaignEnrollment("external_id_2", null);
        List<CampaignEnrollment> enrollments = asList(enrollment1, enrollment2);

        when(mockCampaignEnrollmentService.search(enrollmentQuery)).thenReturn(enrollments);
        CampaignEnrollmentRecord record1 = new CampaignEnrollmentRecord(null, null, null, null);
        CampaignEnrollmentRecord record2 = new CampaignEnrollmentRecord(null, null, null, null);
        when(mockCampaignEnrollmentRecordMapper.map(enrollment1)).thenReturn(record1);
        when(mockCampaignEnrollmentRecordMapper.map(enrollment2)).thenReturn(record2);

        assertEquals(asList(new CampaignEnrollmentRecord[]{record1, record2}), messageCampaignService.search(enrollmentQuery));
    }

    @Test
    public void shouldGetCampaignTimings() {
        Date startDate = DateTime.now().plusDays(1).toDate();
        Date endDate = DateTime.now().plusDays(1).plusDays(5).toDate();
        AbsoluteCampaign campaign = mock(AbsoluteCampaign.class);

        String campaignName = "campaign";
        when(allMessageCampaigns.get(campaignName)).thenReturn(campaign);
        when(campaign.getScheduler(eq(schedulerService), eq(mockCampaignEnrollmentService), any(CampaignRequest.class))).thenReturn(scheduler);

        messageCampaignService.getCampaignTimings("externalId", campaignName, startDate, endDate);

        verify(scheduler).getCampaignTimings(startDate, endDate);
    }
}
