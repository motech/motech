package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.contract.EnrollForRelativeProgramRequest;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatingCampaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.factory.WallTimeFactory;

import java.util.HashMap;

public class RepeatingProgramScheduler extends MessageCampaignScheduler {

    private EnrollForRelativeProgramRequest enrollRequest;
    private RepeatingCampaign campaign;

    public RepeatingProgramScheduler(MotechSchedulerService schedulerService, EnrollForRelativeProgramRequest enrollRequest, RepeatingCampaign campaign) {
        this.campaign = campaign;
        this.schedulerService = schedulerService;
        this.enrollRequest = enrollRequest;
    }

    @Override
    public void scheduleJob(CampaignMessage message) {

        RepeatingCampaign repeatingCampaign = campaign;
        RepeatingCampaignMessage repeatingCampaignMessage = (RepeatingCampaignMessage) message;

        scheduleRepeatingJobs(repeatingCampaign.maxDuration(), repeatingCampaignMessage, campaign.name());
    }

    private void scheduleRepeatingJobs(String maxDuration, RepeatingCampaignMessage message, String campaignName) {

        DateTime startDate = new DateTime(enrollRequest.referenceDate());
        WallTime duration = WallTimeFactory.create(maxDuration);
        DateTime endDate = startDate.plusDays(duration.inDays());
        int repeatIntervalInDays = WallTimeFactory.create(message.repeatInterval()).inDays();

        DateTime jobDate = startDate;
        while (jobDate.isBefore(endDate)) {
            String jobId = campaignName + "_" + message.name() + "_" + enrollRequest.externalId() + "_" + jobDate.toString("yyyy-mm-dd");
            HashMap jobParams = new SchedulerPayloadBuilder()
                    .withJobId(jobId)
                    .withCampaignName(campaignName)
                    .withExternalId(enrollRequest.externalId())
                    .payload();

            scheduleJobOn(enrollRequest.reminderTime(), jobDate.toDate(), jobParams);
            jobDate = jobDate.plusDays(repeatIntervalInDays);
        }
    }

}
