package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.contract.EnrollForAbsoluteProgramRequest;
import org.motechproject.server.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.server.messagecampaign.domain.message.AbsoluteCampaignMessage;

import java.util.HashMap;

public class AbsoluteProgramScheduler extends MessageCampaignScheduler {

    private EnrollForAbsoluteProgramRequest enrollRequest;
    private AbsoluteCampaign campaign;

    public AbsoluteProgramScheduler(MotechSchedulerService schedulerService,
                                    EnrollForAbsoluteProgramRequest request, AbsoluteCampaign campaign) {
        this.campaign = campaign;
        this.schedulerService = schedulerService;
        this.enrollRequest = request;
    }

    private void scheduleJob(AbsoluteCampaignMessage message) {
        String jobId = campaign + "_" + message.name() + "_" + enrollRequest.externalId();

        HashMap params = new SchedulerPayloadBuilder()
                .withJobId(jobId)
                .withCampaignName(campaign.name())
                .withExternalId(enrollRequest.externalId())
                .payload();

        scheduleJobOn(enrollRequest.reminderTime(), message.date(), params);
    }

    @Override
    public void scheduleJobs() {
        for (AbsoluteCampaignMessage message : campaign.messages()) {
            scheduleJob(message);
        }
    }
}
