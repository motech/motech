package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.motechproject.server.messagecampaign.domain.campaign.CronBasedCampaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.CronBasedCampaignMessage;

public class CronBasedProgramScheduler extends MessageCampaignScheduler {

    public CronBasedProgramScheduler(MotechSchedulerService schedulerService, EnrollRequest enrollRequest, CronBasedCampaign campaign) {
        super(schedulerService, enrollRequest, campaign);
    }

    @Override
    protected void scheduleJob(CampaignMessage message) {
        CronBasedCampaignMessage cronBasedCampaignMessage = (CronBasedCampaignMessage) message;

        scheduleJobOn(cronBasedCampaignMessage.cron(), enrollRequest.referenceDate(), jobParams(message));
    }
}
