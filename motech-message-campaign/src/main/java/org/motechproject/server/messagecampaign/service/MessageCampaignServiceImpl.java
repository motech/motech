package org.motechproject.server.messagecampaign.service;

import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.MessageCampaignException;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.scheduler.MessageCampaignScheduler;
import org.motechproject.server.messagecampaign.scheduler.MessageCampaignSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
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
        MessageCampaignScheduler scheduler = getScheduler(enrollRequest);

        scheduler.scheduleJobs();
    }

    public void reEnroll(EnrollRequest enrollRequest) {
        MessageCampaignScheduler scheduler = getScheduler(enrollRequest);

        scheduler.rescheduleJobs();
    }

    private MessageCampaignScheduler getScheduler(EnrollRequest enrollRequest) {
        Campaign<CampaignMessage> campaign = allMessageCampaigns.get(enrollRequest.campaignName());

        if (campaign == null)
            throw new MessageCampaignException("No campaign by name : " + enrollRequest.campaignName());

        return schedulerFactory.scheduler(enrollRequest, campaign);
    }
}