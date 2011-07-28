package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.contract.EnrollForAbsoluteProgramRequest;
import org.motechproject.server.messagecampaign.domain.CampaignMessage;

import java.util.HashMap;

public class AbsoluteProgramScheduler extends MessageCampaignScheduler {

    private EnrollForAbsoluteProgramRequest enrollRequest;

    public AbsoluteProgramScheduler(MotechSchedulerService schedulerService,
                                    EnrollForAbsoluteProgramRequest request) {
        this.schedulerService = schedulerService;
        enrollRequest = request;
    }

    @Override
    public void scheduleJob(String campaignName, CampaignMessage message) {
        String jobId = campaignName + "_" + message.name() + "_" + enrollRequest.externalId();

        HashMap params = new SchedulerPayloadBuilder()
                .withJobId(jobId)
                .withCampaignName(campaignName)
                .withExternalId(enrollRequest.externalId())
                .payload();

        scheduleJob(message.date(), enrollRequest.reminderTime(), params);
    }


}
