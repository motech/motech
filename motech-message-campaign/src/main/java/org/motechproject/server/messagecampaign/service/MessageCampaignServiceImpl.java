package org.motechproject.server.messagecampaign.service;

import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.MessageCampaignException;
import org.motechproject.server.messagecampaign.scheduler.MessageCampaignScheduler;
import org.motechproject.server.messagecampaign.scheduler.MessageCampaignSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MessageCampaignServiceImpl implements MessageCampaignService {

    private AllMessageCampaigns allMessageCampaigns;
    private MessageCampaignSchedulerFactory schedulerFactory;

    @Autowired
    public MessageCampaignServiceImpl(AllMessageCampaigns allMessageCampaigns,
                                      MessageCampaignSchedulerFactory schedulerFactory) {
        this.allMessageCampaigns = allMessageCampaigns;
        this.schedulerFactory = schedulerFactory;
    }

    public void enroll(EnrollRequest enrollRequest) {
        AbsoluteCampaign campaign = (AbsoluteCampaign) allMessageCampaigns.get(enrollRequest.campaignName());

        if(campaign == null) throw new MessageCampaignException("No campaign by name : "+enrollRequest.campaignName());

        for (CampaignMessage message : campaign.messages()) {
            MessageCampaignScheduler scheduler = schedulerFactory.scheduler(enrollRequest);
            scheduler.scheduleJob(campaign, message);
        }
    }
}