package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatingCampaign;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.factory.WallTimeFactory;

import java.util.HashMap;

public class RepeatingProgramScheduler extends MessageCampaignScheduler {

    private EnrollRequest enrollRequest;
    private RepeatingCampaign campaign;

    public RepeatingProgramScheduler(MotechSchedulerService schedulerService, EnrollRequest enrollRequest, RepeatingCampaign campaign) {
        this.campaign = campaign;
        this.schedulerService = schedulerService;
        this.enrollRequest = enrollRequest;
    }

    private void scheduleJob(RepeatingCampaignMessage message) {
        DateTime startDate = new DateTime(enrollRequest.referenceDate());
        WallTime duration = WallTimeFactory.create(campaign.maxDuration());
        DateTime endDate = startDate.plusDays(duration.inDays());
        int repeatIntervalInDays = WallTimeFactory.create(message.repeatInterval()).inDays();

        DateTime jobDate = startDate;
        Integer index = 1;
        while (jobDate.isBefore(endDate)) {
            String messageKey = message.messageKey().replace("{Offset}", index.toString());
            String jobId = EventKeys.BASE_SUBJECT + campaign.name() + "." + message.name() + "." + enrollRequest.externalId()+"."+index;
            HashMap jobParams = new SchedulerPayloadBuilder()
                    .withJobId(jobId)
                    .withCampaignName(campaign.name())
                    .withExternalId(enrollRequest.externalId())
                    .withMessageKey(messageKey)
                    .payload();

            scheduleJobOn(enrollRequest.reminderTime(), jobDate.toDate(), jobParams);
            jobDate = jobDate.plusDays(repeatIntervalInDays);
            index++;
        }
    }

    @Override
    public void scheduleJobs(){
        for(RepeatingCampaignMessage repeatingCampaignMessage : campaign.messages()){
            scheduleJob(repeatingCampaignMessage);
        }
    }


}
