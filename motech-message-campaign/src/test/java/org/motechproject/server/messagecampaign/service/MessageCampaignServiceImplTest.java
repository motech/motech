package org.motechproject.server.messagecampaign.service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.messagecampaign.contract.EnrollForAbsoluteProgramRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.MessageCampaignException;
import org.motechproject.server.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.server.messagecampaign.domain.message.AbsoluteCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;
import org.motechproject.server.messagecampaign.scheduler.AbsoluteProgramScheduler;
import org.motechproject.server.messagecampaign.scheduler.MessageCampaignSchedulerFactory;

import java.util.LinkedList;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessageCampaignServiceImplTest {

    @Mock
    private AllMessageCampaigns allMessageCampaigns;

    @Mock
    private MessageCampaignSchedulerFactory schedulerFactory;

    @Before
    public void setUp() {
        allMessageCampaigns = mock(AllMessageCampaigns.class);
        schedulerFactory = mock(MessageCampaignSchedulerFactory.class);
        initMocks(this);
    }

    @Test
    public void enrollTest() {
        String campaignName = "campaign-name";
        EnrollForAbsoluteProgramRequest enrollRequest = new EnrollForAbsoluteProgramRequest();
        MessageCampaignServiceImpl messageCampaignService = new MessageCampaignServiceImpl(allMessageCampaigns, schedulerFactory);
        AbsoluteProgramScheduler mockAbsoluteProgramScheduler = mock(AbsoluteProgramScheduler.class);
        final AbsoluteCampaign absoluteCampaign = new AbsoluteCampaign();
        final AbsoluteCampaignMessage absoluteCampaignMessage = new AbsoluteCampaignMessage();

        absoluteCampaign.messages(new LinkedList<AbsoluteCampaignMessage>() {
            {
                add(absoluteCampaignMessage);
            }
        });
        enrollRequest.campaignName(campaignName);
        when(allMessageCampaigns.get(campaignName)).thenReturn(absoluteCampaign);
        when(schedulerFactory.scheduler(enrollRequest, absoluteCampaign)).thenReturn(mockAbsoluteProgramScheduler);

        messageCampaignService.enroll(enrollRequest);
        verify(mockAbsoluteProgramScheduler, times(1)).scheduleJob(absoluteCampaignMessage);
    }


    @Test(expected = MessageCampaignException.class)
    public void enrollWithUnknownCampaignTest() {
        String campaignName = "non-existent-campaign-name";
        EnrollForAbsoluteProgramRequest enrollRequest = new EnrollForAbsoluteProgramRequest();
        MessageCampaignServiceImpl messageCampaignService = new MessageCampaignServiceImpl(allMessageCampaigns, schedulerFactory);
        enrollRequest.campaignName(campaignName);

        when(allMessageCampaigns.get(campaignName)).thenReturn(null);

        messageCampaignService.enroll(enrollRequest);
    }

    @Test(expected = MessageCampaignException.class)
    @Ignore
    public void enrollWithNonMatchingRequestTest() {
        String campaignName = "campaign-name";
        EnrollForAbsoluteProgramRequest enrollRequest = new EnrollForAbsoluteProgramRequest();
        MessageCampaignServiceImpl messageCampaignService = new MessageCampaignServiceImpl(allMessageCampaigns, schedulerFactory);
        final OffsetCampaign offsetCampaign = new OffsetCampaign();
        final OffsetCampaignMessage offsetCampaignMessage = new OffsetCampaignMessage();

        offsetCampaign.messages(new LinkedList<OffsetCampaignMessage>() {
            {
                add(offsetCampaignMessage);
            }
        });
        enrollRequest.campaignName(campaignName);
        when(allMessageCampaigns.get(campaignName)).thenReturn(offsetCampaign);

        messageCampaignService.enroll(enrollRequest);
    }
}
