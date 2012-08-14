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

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

@Component
public class CampaignSchedulerFactory {
    private static final int CAMPAIGN_SCHEDULER_SERVICE_COUNT = 5;
    private Map<Class, CampaignSchedulerService> map;

    @Autowired
    private CampaignSchedulerService repeatIntervalCampaignSchedulerService;

    @Autowired
    private CampaignSchedulerService dayOfWeekCampaignSchedulerService;

    @Autowired
    private AllMessageCampaigns allMessageCampaigns;

    @PostConstruct
    private void setUp() {
        map = new HashMap<>(CAMPAIGN_SCHEDULER_SERVICE_COUNT);
        map.put(AbsoluteCampaign.class, repeatIntervalCampaignSchedulerService);
        map.put(OffsetCampaign.class, repeatIntervalCampaignSchedulerService);
        map.put(CronBasedCampaign.class, repeatIntervalCampaignSchedulerService);
        map.put(RepeatIntervalCampaign.class, repeatIntervalCampaignSchedulerService);
        map.put(DayOfWeekCampaign.class, dayOfWeekCampaignSchedulerService);
    }

    public CampaignSchedulerService getCampaignScheduler(final String campaignName) {
        CampaignSchedulerService schedulerService = map.get(allMessageCampaigns.get(campaignName).getClass());

        if (schedulerService == null) {
            throw new CampaignNotFoundException(format("Campaign (%s) not found.", campaignName));
        }

        return schedulerService;
    }
}
