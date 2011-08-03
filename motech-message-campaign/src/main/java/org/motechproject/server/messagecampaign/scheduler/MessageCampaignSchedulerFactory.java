package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.motechproject.server.messagecampaign.domain.MessageCampaignException;
import org.motechproject.server.messagecampaign.domain.campaign.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageCampaignSchedulerFactory {

    private MotechSchedulerService schedulerService;

    @Autowired
    public MessageCampaignSchedulerFactory(MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public MessageCampaignScheduler scheduler(EnrollRequest enrollRequest, Campaign campaign) {
        if (campaign instanceof AbsoluteCampaign)
            return new AbsoluteProgramScheduler(schedulerService, enrollRequest, (AbsoluteCampaign) campaign);
        else if (campaign instanceof OffsetCampaign)
            return new OffsetProgramScheduler(schedulerService, enrollRequest, (OffsetCampaign) campaign);
        else if (campaign instanceof RepeatingCampaign)
            return new RepeatingProgramScheduler(schedulerService, enrollRequest, (RepeatingCampaign) campaign);
        else if (campaign instanceof CronBasedCampaign)
            return new CronBasedProgramScheduler(schedulerService, enrollRequest, (CronBasedCampaign) campaign);
        throw new MessageCampaignException("Cannot find a scheduler for campaign : " + campaign.name());
    }
}
