package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.LocalDate;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatingCampaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.factory.WallTimeFactory;

public class RepeatingProgramScheduler extends MessageCampaignScheduler {

    public RepeatingProgramScheduler(MotechSchedulerService schedulerService, EnrollRequest enrollRequest, RepeatingCampaign campaign) {
        super(schedulerService, enrollRequest, campaign);
    }

    @Override
    protected void scheduleJob(CampaignMessage message) {
        LocalDate startDate = referenceDate();
        WallTime duration = WallTimeFactory.create(((RepeatingCampaign)campaign).maxDuration());
        LocalDate endDate = startDate.plusDays(duration.inDays());
        int repeatIntervalInDays = WallTimeFactory.create(((RepeatingCampaignMessage)message).repeatInterval()).inDays();

        LocalDate jobDate = startDate;
        Integer index = 1;
        while (jobDate.isBefore(endDate)) {
            String messageKey = message.messageKey().replace("{Offset}", index.toString());
            scheduleJobOn(enrollRequest.reminderTime(), jobDate, jobParams(messageKey));
            jobDate = jobDate.plusDays(repeatIntervalInDays);
            index++;
        }
    }
}
