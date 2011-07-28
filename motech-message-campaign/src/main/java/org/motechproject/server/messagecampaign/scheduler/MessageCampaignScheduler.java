package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.server.messagecampaign.domain.CampaignMessage;

public interface MessageCampaignScheduler {
    public static final int REPEAT_WINDOW_IN_HOURS = 2;
    public static final int REPEAT_INTERVAL_IN_MINUTES = 15;

    public void scheduleJob(String campaignName, CampaignMessage message);
}

