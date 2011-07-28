package org.motechproject.server.messagecampaign.service;

import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.Campaign;
import org.motechproject.server.messagecampaign.domain.CampaignMessage;
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

    @Override
    public void enroll(EnrollRequest enrollRequest) {
        Campaign campaign = allMessageCampaigns.get(enrollRequest.campaignName());

        for (CampaignMessage message : campaign.getMessages()) {
            MessageCampaignScheduler scheduler = schedulerFactory.scheduler(enrollRequest);
            scheduler.scheduleJob(campaign.getName(), message);
        }
    }
}