package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.CampaignNotFoundException;
import org.motechproject.server.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.CronBasedCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.DayOfWeekCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatIntervalCampaign;
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

    public CampaignSchedulerService getCampaignScheduler(String campaignName) {
        //TODO: Map should not be created every time
        HashMap<Class, CampaignSchedulerService> map = new HashMap<>();
        map.put(AbsoluteCampaign.class, repeatIntervalCampaignSchedulerService);
        map.put(OffsetCampaign.class, repeatIntervalCampaignSchedulerService);
        map.put(CronBasedCampaign.class, repeatIntervalCampaignSchedulerService);
        map.put(RepeatIntervalCampaign.class, repeatIntervalCampaignSchedulerService);
        map.put(DayOfWeekCampaign.class, dayOfWeekCampaignSchedulerService);

        CampaignSchedulerService schedulerService = map.get(allMessageCampaigns.get(campaignName).getClass());
        if (schedulerService == null) {
            throw new CampaignNotFoundException(format("Campaign (%s) not found.", campaignName));
        }
        return schedulerService;
    }
}
