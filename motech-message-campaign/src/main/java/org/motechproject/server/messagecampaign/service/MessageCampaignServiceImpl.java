package org.motechproject.server.messagecampaign.service;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.MessageCampaignException;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.scheduler.MessageCampaignScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageCampaignServiceImpl implements MessageCampaignService {
    private MotechSchedulerService schedulerService;
    private AllMessageCampaigns allMessageCampaigns;

    @Autowired
    public MessageCampaignServiceImpl(AllMessageCampaigns allMessageCampaigns, MotechSchedulerService schedulerService) {
        this.allMessageCampaigns = allMessageCampaigns;
        this.schedulerService = schedulerService;
    }

    public void startFor(CampaignRequest request) {
        getCampaignFor(request).start();
    }

    public void restartFor(CampaignRequest request) {
        getCampaignFor(request).restart();
    }

    public void stopFor(CampaignRequest request) {
        getCampaignFor(request).stop();
    }

    private MessageCampaignScheduler getCampaignFor(CampaignRequest enrollRequest) {
        Campaign<CampaignMessage> campaign = allMessageCampaigns.get(enrollRequest.campaignName());
        if (campaign == null)
            throw new MessageCampaignException("No campaign by name : " + enrollRequest.campaignName());
        return campaign.getScheduler(schedulerService, enrollRequest);
    }
}