package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.contract.EnrollForAbsoluteProgramRequest;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.motechproject.server.messagecampaign.domain.message.AbsoluteCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;

import java.util.HashMap;

public class AbsoluteProgramScheduler extends MessageCampaignScheduler {

    private EnrollForAbsoluteProgramRequest enrollRequest;

    public AbsoluteProgramScheduler(MotechSchedulerService schedulerService,
                                    EnrollForAbsoluteProgramRequest request) {
        this.schedulerService = schedulerService;
        enrollRequest = request;
    }

    @Override
    public void scheduleJob(Campaign campaign, CampaignMessage message) {
        AbsoluteCampaignMessage absoluteCampaignMessage = (AbsoluteCampaignMessage) message;
        String jobId = campaign + "_" + message.name() + "_" + enrollRequest.externalId();

        HashMap params = new SchedulerPayloadBuilder()
                .withJobId(jobId)
                .withCampaignName(campaign.name())
                .withExternalId(enrollRequest.externalId())
                .payload();

        scheduleJobOn(enrollRequest.reminderTime(), absoluteCampaignMessage.date(), params);
    }
}
