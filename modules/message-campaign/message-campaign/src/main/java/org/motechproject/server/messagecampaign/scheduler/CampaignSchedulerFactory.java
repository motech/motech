package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.CampaignNotFoundException;
import org.motechproject.server.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.motechproject.server.messagecampaign.domain.campaign.CronBasedCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.DayOfWeekCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatIntervalCampaign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

import static java.lang.String.format;

@Component
public class CampaignSchedulerFactory {

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

    private HashMap<Class, CampaignSchedulerService> campaignSchedulerServices = new HashMap<>();

    @PostConstruct
    private void init() {
        campaignSchedulerServices.put(AbsoluteCampaign.class, absoluteCampaignSchedulerService);
        campaignSchedulerServices.put(OffsetCampaign.class, offsetCampaignSchedulerService);
        campaignSchedulerServices.put(CronBasedCampaign.class, cronBasedCampaignSchedulerService);
        campaignSchedulerServices.put(RepeatIntervalCampaign.class, repeatIntervalCampaignSchedulerService);
        campaignSchedulerServices.put(DayOfWeekCampaign.class, dayOfWeekCampaignSchedulerService);
    }


    public CampaignSchedulerService getCampaignScheduler(String campaignName) {
        final Campaign campaign = allMessageCampaigns.get(campaignName);
        CampaignSchedulerService schedulerService = campaignSchedulerServices.get(campaign.getClass());
        if (schedulerService == null) {
            throw new CampaignNotFoundException(format("Campaign (%s) not found.", campaignName));
        }
        return schedulerService;
    }
}
