package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.EnrollForAbsoluteProgramRequest;
import org.motechproject.server.messagecampaign.contract.EnrollForCronBasedProgramRequest;
import org.motechproject.server.messagecampaign.contract.EnrollForRelativeProgramRequest;
import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.motechproject.server.messagecampaign.domain.campaign.*;
import org.springframework.beans.factory.annotation.Autowired;

public class MessageCampaignSchedulerFactory {

    @Autowired
    private MotechSchedulerService schedulerService;

    public MessageCampaignSchedulerFactory(MotechSchedulerService schedulerService){
        this.schedulerService = schedulerService;
    }

    public MessageCampaignScheduler scheduler(EnrollRequest enrollRequest, Campaign campaign) {
        if(campaign.getClass().equals(AbsoluteCampaign.class))
            return new AbsoluteProgramScheduler(schedulerService, (EnrollForAbsoluteProgramRequest) enrollRequest, (AbsoluteCampaign) campaign);
        else if(campaign.getClass().equals(OffsetCampaign.class))
            return new OffsetProgramScheduler(schedulerService, (EnrollForRelativeProgramRequest) enrollRequest, (OffsetCampaign) campaign);
        else if(campaign.getClass().equals(RepeatingCampaign.class))
            return new RepeatingProgramScheduler(schedulerService, (EnrollForRelativeProgramRequest) enrollRequest, (RepeatingCampaign) campaign);
        else if(campaign.getClass().equals(CronBasedCampaign.class))
            return new CronBasedProgramScheduler(schedulerService, (EnrollForCronBasedProgramRequest) enrollRequest, (CronBasedCampaign) campaign);
        return null;
    }
}
