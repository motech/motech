package org.motechproject.server.messagecampaign.service;

import org.motechproject.builder.CronJobExpressionBuilder;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.Campaign;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class MessageCampaignServiceImpl implements MessageCampaignService {

    public static final int REPEAT_WINDOW_IN_HOURS = 2;
    public static final int REPEAT_INTERVAL_IN_MINUTES = 15;
    private AllMessageCampaigns allMessageCampaigns;

    private MotechSchedulerService schedulerService;

    @Autowired
    public MessageCampaignServiceImpl(AllMessageCampaigns allMessageCampaigns, MotechSchedulerService schedulerService) {
        this.allMessageCampaigns = allMessageCampaigns;
        this.schedulerService = schedulerService;
    }

    @Override
    public void enroll(String campaignName, int startHour, int startMinute) {
        Campaign campaign = allMessageCampaigns.get(campaignName);
        Map<String, Object> params = new SchedulerPayloadBuilder()
                .withJobId("Id").withMessageCampaignId("mcId").payload();

        Time startTime = new Time(startHour, startMinute);

//        TODO
        String cronJobExpression = new CronJobExpressionBuilder(startTime,
                REPEAT_WINDOW_IN_HOURS, REPEAT_INTERVAL_IN_MINUTES).build();

        MotechEvent motechEvent = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_EVENT_SUBJECT, params);

//        TODO - Hardcoded start and end date for now
        Calendar calendar = Calendar.getInstance();
        Date startDate = calendar.getTime();
        calendar.add(Calendar.DATE, 10);
        Date endDate = calendar.getTime();

        CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, startDate, endDate);
        schedulerService.scheduleJob(schedulableJob);
    }
}
