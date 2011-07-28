package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.EnrollForAbsoluteProgramRequest;
import org.motechproject.server.messagecampaign.contract.EnrollForRelativeProgramRequest;
import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.springframework.beans.factory.annotation.Autowired;

public class MessageCampaignSchedulerFactory {

    @Autowired
    private MotechSchedulerService schedulerService;

    public MessageCampaignSchedulerFactory(MotechSchedulerService schedulerService){
        this.schedulerService = schedulerService;
    }

    public MessageCampaignScheduler scheduler(EnrollRequest enrollRequest) {
        if(enrollRequest.getClass().equals(EnrollForAbsoluteProgramRequest.class))
            return new AbsoluteProgramCampaignScheduler(schedulerService, (EnrollForAbsoluteProgramRequest) enrollRequest);
        else if(enrollRequest.getClass().equals(EnrollForRelativeProgramRequest.class))
            return new RelativeProgramCampaignScheduler(schedulerService, (EnrollForRelativeProgramRequest) enrollRequest);
        return null;
    }
}
