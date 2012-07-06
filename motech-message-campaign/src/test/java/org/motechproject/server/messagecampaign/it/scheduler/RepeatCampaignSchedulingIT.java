package org.motechproject.server.messagecampaign.it.scheduler;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.motechproject.testing.utils.TimeFaker.fakeToday;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.quartz.TriggerKey.triggerKey;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:repeat_campaign_it/context.xml")
public class RepeatCampaignSchedulingIT {

    @Autowired
    MessageCampaignService messageCampaignService;

    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;

    Scheduler scheduler;

    @Autowired
    private MotechSchedulerService schedulerService;

    @Before
    public void setup() {
        scheduler = schedulerFactoryBean.getScheduler();
    }

    @After
    public void teardown() {
        schedulerService.unscheduleAllJobs("org.motechproject.server.messagecampaign");
    }

    @Test
    public void shouldScheduleAllMessagesOfCampaignAtMessageStartTime() throws SchedulerException {
        CampaignRequest campaignRequest = new CampaignRequest("entity_1", "WeeklyCampaign", new LocalDate(2020, 7, 10), null);
        messageCampaignService.startFor(campaignRequest);
        List<DateTime> fireTimes = getFireTimes("org.motechproject.server.messagecampaign.fired-campaign-message-MessageJob.WeeklyCampaign.entity_1.message_key_1-repeat");
        assertEquals(asList(
            newDateTime(2020, 7, 10, 10, 30, 0),
            newDateTime(2020, 7, 17, 10, 30, 0),
            newDateTime(2020, 7, 24, 10, 30, 0)),
            fireTimes);

        fireTimes = getFireTimes("org.motechproject.server.messagecampaign.fired-campaign-message-MessageJob.WeeklyCampaign.entity_1.message_key_2-repeat");
        assertEquals(asList(
            newDateTime(2020, 7, 10, 8, 30, 0),
            newDateTime(2020, 7, 20, 8, 30, 0),
            newDateTime(2020, 7, 30, 8, 30, 0)),
            fireTimes);
    }

    @Test
    public void shouldScheduleWeeklyMessagesAtUserSpecifiedTime() throws SchedulerException {
        CampaignRequest campaignRequest = new CampaignRequest("entity_1", "WeeklyCampaign", new LocalDate(2020, 7, 10), new Time(15, 20));
        messageCampaignService.startFor(campaignRequest);
        List<DateTime> fireTimes = getFireTimes("org.motechproject.server.messagecampaign.fired-campaign-message-MessageJob.WeeklyCampaign.entity_1.message_key_1-repeat");
        assertEquals(asList(
            newDateTime(2020, 7, 10, 15, 20, 0),
            newDateTime(2020, 7, 17, 15, 20, 0),
            newDateTime(2020, 7, 24, 15, 20, 0)),
            fireTimes);
    }

    @Test
    public void shouldNotScheduleMessagesInPastForDelayedEnrollment() throws SchedulerException {
        try {
            fakeToday(new LocalDate(2020, 7, 15));
            CampaignRequest campaignRequest = new CampaignRequest("entity_1", "WeeklyCampaign", new LocalDate(2020, 7, 10), null);
            messageCampaignService.startFor(campaignRequest);
            List<DateTime> fireTimes = getFireTimes("org.motechproject.server.messagecampaign.fired-campaign-message-MessageJob.WeeklyCampaign.entity_1.message_key_1-repeat");
            assertEquals(asList(
                newDateTime(2020, 7, 17, 10, 30, 0),
                newDateTime(2020, 7, 24, 10, 30, 0)),
                fireTimes);
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldScheduleMessagesEvery12Hours() throws SchedulerException {
        CampaignRequest campaignRequest = new CampaignRequest("entity_1", "HourlyCampaign", new LocalDate(2020, 7, 10), null);
        messageCampaignService.startFor(campaignRequest);
        List<DateTime> fireTimes = getFireTimes("org.motechproject.server.messagecampaign.fired-campaign-message-MessageJob.HourlyCampaign.entity_1.message_key_1-repeat");
        assertEquals(asList(
            newDateTime(2020, 7, 10, 10, 30, 0),
            newDateTime(2020, 7, 10, 22, 30, 0),
            newDateTime(2020, 7, 11, 10, 30, 0),
            newDateTime(2020, 7, 11, 22, 30, 0)),
            fireTimes);
    }

    private List<DateTime> getFireTimes(String triggerKey) throws SchedulerException {
        Trigger trigger = scheduler.getTrigger(triggerKey(triggerKey, "default"));
        List<DateTime> fireTimes = new ArrayList<>();
        Date nextFireTime = trigger.getNextFireTime();
        while (nextFireTime != null) {
            fireTimes.add(newDateTime(nextFireTime));
            nextFireTime = trigger.getFireTimeAfter(nextFireTime);
        }
        return fireTimes;
    }
}
