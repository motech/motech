package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatingCampaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.factory.WallTimeFactory;

public class RepeatingProgramScheduler extends MessageCampaignScheduler {

    public RepeatingProgramScheduler(MotechSchedulerService schedulerService, CampaignRequest enrollRequest, RepeatingCampaign campaign) {
        super(schedulerService, enrollRequest, campaign);
    }

    @Override
    protected void scheduleJobFor(CampaignMessage message) {
        LocalDate startDate = referenceDate();
        WallTime duration = WallTimeFactory.create(((RepeatingCampaign)campaign).maxDuration());
        LocalDate endDate = startDate.plusDays(duration.inDays());
        int repeatIntervalInDays = WallTimeFactory.create(((RepeatingCampaignMessage)message).repeatInterval()).inDays();

        long repeatInterval = Days.days(repeatIntervalInDays).toStandardSeconds().getSeconds() * 1000L;
        scheduleRepeatingJob(startDate, endDate, repeatInterval, jobParams(message.messageKey()));
    }
}
