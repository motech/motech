package org.motechproject.server.messagecampaign.domain.campaign;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.message.CronBasedCampaignMessage;
import org.motechproject.server.messagecampaign.scheduler.CronBasedProgramScheduler;
import org.motechproject.server.messagecampaign.scheduler.MessageCampaignScheduler;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;

import java.util.List;

public class CronBasedCampaign extends Campaign<CronBasedCampaignMessage> {

    private String maxDuration;
    private List<CronBasedCampaignMessage> messages;

    @Override
    public List<CronBasedCampaignMessage> messages() {
        return this.messages;
    }

    @Override
    public MessageCampaignScheduler getScheduler(MotechSchedulerService schedulerService, CampaignEnrollmentService campaignEnrollmentService, CampaignRequest enrollRequest) {
        return new CronBasedProgramScheduler(schedulerService, enrollRequest, this,campaignEnrollmentService);
    }

    @Override
    public void setMessages(List<CronBasedCampaignMessage> messages) {
        this.messages = messages;
    }


    public void maxDuration(String maxDuration) {
        this.maxDuration = maxDuration;
    }

    public String maxDuration() {
        return maxDuration;
    }
}
