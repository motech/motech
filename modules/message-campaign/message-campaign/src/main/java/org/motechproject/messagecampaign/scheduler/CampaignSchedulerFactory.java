package org.motechproject.messagecampaign.scheduler;

import org.motechproject.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.messagecampaign.domain.CampaignNotFoundException;
import org.motechproject.messagecampaign.domain.campaign.Campaign;
import org.motechproject.messagecampaign.domain.campaign.DayOfWeekCampaign;
import org.motechproject.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.messagecampaign.domain.campaign.RepeatIntervalCampaign;
import org.motechproject.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.messagecampaign.domain.campaign.CronBasedCampaign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

@Component
public class CampaignSchedulerFactory {
    private static final int CAMPAIGN_SCHEDULER_SERVICE_COUNT = 5;
    private Map<Class, CampaignSchedulerService> campaignSchedulerServices = new HashMap<>(CAMPAIGN_SCHEDULER_SERVICE_COUNT);

    @Autowired
    private AbsoluteCampaignSchedulerService absoluteCampaignSchedulerService;

    @Autowired
    private OffsetCampaignSchedulerService offsetCampaignSchedulerService;

    @Autowired
    private CronBasedCampaignSchedulerService cronBasedCampaignSchedulerService;

    @Autowired
    private RepeatIntervalCampaignSchedulerService repeatIntervalCampaignSchedulerService;

    @Autowired
    private DayOfWeekCampaignSchedulerService dayOfWeekCampaignSchedulerService;

    @Autowired
    private AllMessageCampaigns allMessageCampaigns;

    @PostConstruct
    public void init() {
        campaignSchedulerServices.put(AbsoluteCampaign.class, absoluteCampaignSchedulerService);
        campaignSchedulerServices.put(OffsetCampaign.class, offsetCampaignSchedulerService);
        campaignSchedulerServices.put(CronBasedCampaign.class, cronBasedCampaignSchedulerService);
        campaignSchedulerServices.put(RepeatIntervalCampaign.class, repeatIntervalCampaignSchedulerService);
        campaignSchedulerServices.put(DayOfWeekCampaign.class, dayOfWeekCampaignSchedulerService);
    }


    public CampaignSchedulerService getCampaignScheduler(final String campaignName) {
        final Campaign campaign = allMessageCampaigns.getCampaign(campaignName);

        if (campaign == null) {
            throw new CampaignNotFoundException(format("Campaign (%s) not found.", campaignName));
        }

        CampaignSchedulerService schedulerService = campaignSchedulerServices.get(campaign.getClass());

        if (schedulerService == null) {
            throw new CampaignNotFoundException(format("Campaign (%s) not found.", campaignName));
        }

        return schedulerService;
    }
}
