package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.builder.CronJobExpressionBuilder;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.contract.EnrollForRelativeProgramRequest;
import org.motechproject.server.messagecampaign.domain.CampaignMessage;

import java.util.HashMap;

public class RelativeProgramCampaignScheduler implements MessageCampaignScheduler {

    private MotechSchedulerService schedulerService;
    private EnrollForRelativeProgramRequest enrollRequest;

    public RelativeProgramCampaignScheduler(MotechSchedulerService schedulerService, EnrollForRelativeProgramRequest enrollRequest) {
        this.schedulerService = schedulerService;
        this.enrollRequest = enrollRequest;
    }

    @Override
    public void scheduleJob(String campaignName, CampaignMessage message) {
        String jobId = campaignName + "_" + message.name() + "_" + enrollRequest.externalId();
        HashMap params = new SchedulerPayloadBuilder()
                .withJobId(jobId)
                .withCampaignName(campaignName)
                .withExternalId(enrollRequest.externalId())
                .payload();

        String cronJobExpression = new CronJobExpressionBuilder(enrollRequest.reminderTime(),
                REPEAT_WINDOW_IN_HOURS, REPEAT_INTERVAL_IN_MINUTES).build();

        MotechEvent motechEvent = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_EVENT_SUBJECT, params);

        CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, message.date(), null);
        schedulerService.scheduleJob(schedulableJob);
    }
}
