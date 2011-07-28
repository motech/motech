package org.motechproject.server.messagecampaign.service;

import org.joda.time.DateTime;
import org.motechproject.builder.CronJobExpressionBuilder;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.Campaign;
import org.motechproject.server.messagecampaign.domain.CampaignMessage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;

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
    public void enroll(EnrollRequest enrollRequest) {
        Campaign campaign = allMessageCampaigns.get(enrollRequest.campaignName());

        for (CampaignMessage message : campaign.getMessages()) {
            String jobId = campaign.getName() + "_" + message.getName() + "_" + enrollRequest.externalId();

            scheduleJob(jobId, enrollRequest.externalId(), campaign.getName(), enrollRequest.reminderTime(), message.date());
        }
    }

    private void scheduleJob(String jobId, String externalId, String campaignName, Time startTime, Date date) {
        HashMap params = new SchedulerPayloadBuilder()
                .withJobId(jobId)
                .withCampaignName(campaignName)
                .withExternalId(externalId)
                .payload();

        String cronJobExpression = new CronJobExpressionBuilder(startTime,
                REPEAT_WINDOW_IN_HOURS, REPEAT_INTERVAL_IN_MINUTES).build();

        MotechEvent motechEvent = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_EVENT_SUBJECT, params);

        CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, date, null);
        schedulerService.scheduleJob(schedulableJob);
    }

    private Time time(Date date) {
        DateTime dateTime = new DateTime(date);
        return new Time(dateTime.hourOfDay().get(), dateTime.minuteOfDay().get());
    }
}