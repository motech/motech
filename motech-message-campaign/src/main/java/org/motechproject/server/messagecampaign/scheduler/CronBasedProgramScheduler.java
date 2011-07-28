package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.contract.EnrollForCronBasedProgramRequest;
import org.motechproject.server.messagecampaign.domain.Campaign;
import org.motechproject.server.messagecampaign.domain.CampaignMessage;

import java.util.HashMap;

public class CronBasedProgramScheduler extends MessageCampaignScheduler {

    private EnrollForCronBasedProgramRequest enrollRequest;

    public CronBasedProgramScheduler(MotechSchedulerService schedulerService, EnrollForCronBasedProgramRequest enrollRequest) {
        this.schedulerService = schedulerService;
        this.enrollRequest = enrollRequest;
    }

    @Override
    public void scheduleJob(Campaign campaign, CampaignMessage message) {
        String jobId = campaign.getName() + "_" + message.name() + "_" + enrollRequest.externalId();

        HashMap jobParams = new SchedulerPayloadBuilder()
                .withJobId(jobId)
                .withCampaignName(campaign.getName())
                .withExternalId(enrollRequest.externalId())
                .payload();

        scheduleJobOn(message.cron(), enrollRequest.referenceDate(), jobParams);
    }
}
