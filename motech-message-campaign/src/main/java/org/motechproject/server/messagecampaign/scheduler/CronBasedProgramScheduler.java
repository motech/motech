package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.CronBasedCampaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.CronBasedCampaignMessage;

public class CronBasedProgramScheduler extends MessageCampaignScheduler {

    public CronBasedProgramScheduler(MotechSchedulerService schedulerService, CampaignRequest enrollRequest, CronBasedCampaign campaign) {
        super(schedulerService, enrollRequest, campaign);
    }

    @Override
    protected void scheduleJobFor(CampaignMessage message) {
        CronBasedCampaignMessage cronBasedCampaignMessage = (CronBasedCampaignMessage) message;
        scheduleJobOn(cronBasedCampaignMessage.cron(), referenceDate(), jobParams(message.messageKey()));
    }
}
