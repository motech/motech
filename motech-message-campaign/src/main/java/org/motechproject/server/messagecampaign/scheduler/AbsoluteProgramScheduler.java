package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.server.messagecampaign.domain.message.AbsoluteCampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;

import java.util.HashMap;

public class AbsoluteProgramScheduler extends MessageCampaignScheduler<AbsoluteCampaignMessage, AbsoluteCampaign> {

    private CampaignEnrollmentService campaignEnrollmentService;

    public AbsoluteProgramScheduler(MotechSchedulerService schedulerService, CampaignRequest enrollRequest, AbsoluteCampaign campaign, CampaignEnrollmentService campaignEnrollmentService) {
        super(schedulerService, enrollRequest, campaign, campaignEnrollmentService);
    }

    @Override
    protected void scheduleJobFor(AbsoluteCampaignMessage absoluteCampaignMessage) {
        HashMap params = jobParams(absoluteCampaignMessage.messageKey());
        scheduleJobOn(campaignRequest.reminderTime(), absoluteCampaignMessage.date(), params);
    }
}
