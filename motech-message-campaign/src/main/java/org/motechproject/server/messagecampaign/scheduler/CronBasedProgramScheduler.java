package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.contract.EnrollForCronBasedProgramRequest;
import org.motechproject.server.messagecampaign.domain.campaign.CronBasedCampaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.CronBasedCampaignMessage;

import java.util.HashMap;

public class CronBasedProgramScheduler extends MessageCampaignScheduler {

    private EnrollForCronBasedProgramRequest enrollRequest;
    private CronBasedCampaign campaign;

    public CronBasedProgramScheduler(MotechSchedulerService schedulerService, EnrollForCronBasedProgramRequest enrollRequest, CronBasedCampaign campaign) {
        this.campaign = campaign;
        this.schedulerService = schedulerService;
        this.enrollRequest = enrollRequest;
    }

    @Override
    public void scheduleJob(CampaignMessage message) {
        String jobId = campaign.name() + "_" + message.name() + "_" + enrollRequest.externalId();

        HashMap jobParams = new SchedulerPayloadBuilder()
                .withJobId(jobId)
                .withCampaignName(campaign.name())
                .withExternalId(enrollRequest.externalId())
                .payload();

        scheduleJobOn(((CronBasedCampaignMessage)message).cron(), enrollRequest.referenceDate(), jobParams);
    }
}
