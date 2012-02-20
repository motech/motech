package org.motechproject.server.messagecampaign.domain.campaign;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.server.messagecampaign.scheduler.MessageCampaignScheduler;
import org.motechproject.server.messagecampaign.scheduler.RepeatingProgramScheduler;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;

import java.util.List;
import java.util.ResourceBundle;

public class RepeatingCampaign extends Campaign<RepeatingCampaignMessage> {

    private List<RepeatingCampaignMessage> messages;

    private String maxDuration;
    private ResourceBundle resourceBundle = ResourceBundle.getBundle("messageCampaign");
    private final static String REPEATING_CAMPAIGN_24HR_STRATEGY = "24.hour.repeating.campaign.strategy";

    @Override
    public List<RepeatingCampaignMessage> messages() {
        return this.messages;
    }

    @Override
    public MessageCampaignScheduler getScheduler(MotechSchedulerService schedulerService, CampaignEnrollmentService campaignEnrollmentService, CampaignRequest enrollRequest) {
        Boolean dispatchMessagesEvery24Hours = Boolean.valueOf(resourceBundle.getString(REPEATING_CAMPAIGN_24HR_STRATEGY));
        return new RepeatingProgramScheduler(schedulerService, enrollRequest, this, campaignEnrollmentService, dispatchMessagesEvery24Hours);
    }

    @Override
    public void setMessages(List<RepeatingCampaignMessage> messages) {
        this.messages = messages;
    }

    public void maxDuration(String maxDuration) {
        this.maxDuration = maxDuration;
    }

    public String maxDuration() {
        return maxDuration;
    }
}
