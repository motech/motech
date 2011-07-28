package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.builder.CronJobExpressionBuilder;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.domain.CampaignMessage;

import java.util.Date;
import java.util.Map;

public abstract class MessageCampaignScheduler {
    public static final int REPEAT_WINDOW_IN_HOURS = 2;
    public static final int REPEAT_INTERVAL_IN_MINUTES = 15;

    protected MotechSchedulerService schedulerService;

    public abstract void scheduleJob(String campaignName, CampaignMessage message);

    public void scheduleJob(Date date, Time startTime, Map<String, Object> params) {

        String cronJobExpression = new CronJobExpressionBuilder(startTime,
                REPEAT_WINDOW_IN_HOURS, REPEAT_INTERVAL_IN_MINUTES).build();

        MotechEvent motechEvent = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_EVENT_SUBJECT, params);

        CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, date, null);
        schedulerService.scheduleJob(schedulableJob);
    }
}

