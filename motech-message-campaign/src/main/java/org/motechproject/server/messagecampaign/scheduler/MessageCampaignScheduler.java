package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.builder.CronJobExpressionBuilder;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.domain.Campaign;
import org.motechproject.server.messagecampaign.domain.CampaignMessage;

import java.util.Date;
import java.util.Map;

public abstract class MessageCampaignScheduler {
    public static final int REPEAT_WINDOW_IN_HOURS = 2;
    public static final int REPEAT_INTERVAL_IN_MINUTES = 15;

    protected MotechSchedulerService schedulerService;

    public abstract void scheduleJob(Campaign campaign, CampaignMessage message);

    public void scheduleJobOn(Time startTime, Date startDate, Map<String, Object> params) {

        String cronJobExpression = new CronJobExpressionBuilder(startTime,
                REPEAT_WINDOW_IN_HOURS, REPEAT_INTERVAL_IN_MINUTES).build();

        scheduleJobOn(cronJobExpression, startDate, params);
    }

    public void scheduleJobOn(String cronJobExpression, Date startDate, Map<String, Object> params) {
        MotechEvent motechEvent = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_EVENT_SUBJECT, params);

        CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, startDate, null);
        schedulerService.scheduleJob(schedulableJob);

    }


}

