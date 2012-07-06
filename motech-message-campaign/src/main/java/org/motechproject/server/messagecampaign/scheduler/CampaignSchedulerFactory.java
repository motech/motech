package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.CampaignNotFoundException;
import org.motechproject.server.messagecampaign.domain.campaign.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static java.lang.String.format;

@Component
public class CampaignSchedulerFactory {

    @Autowired
    private CampaignSchedulerService repeatIntervalCampaignSchedulerService;
    @Autowired
    private CampaignSchedulerService dayOfWeekCampaignSchedulerService;
    @Autowired
    private AllMessageCampaigns allMessageCampaigns;

    public CampaignSchedulerService getCampaignScheduler(String campaigName) {
        HashMap<Class, CampaignSchedulerService> map = new HashMap<>();
        map.put(AbsoluteCampaign.class, repeatIntervalCampaignSchedulerService);
        map.put(OffsetCampaign.class, repeatIntervalCampaignSchedulerService);
        map.put(CronBasedCampaign.class, repeatIntervalCampaignSchedulerService);
        map.put(RepeatIntervalCampaign.class, repeatIntervalCampaignSchedulerService);
        map.put(DayOfWeekCampaign.class, dayOfWeekCampaignSchedulerService);

        CampaignSchedulerService schedulerService = map.get(allMessageCampaigns.get(campaigName).getClass());
        if (schedulerService == null)
            throw new CampaignNotFoundException(format("Campaign (%s) not found.", campaigName));
        return schedulerService;
    }
}
