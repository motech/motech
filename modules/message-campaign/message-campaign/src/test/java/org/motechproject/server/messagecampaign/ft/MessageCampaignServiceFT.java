package org.motechproject.server.messagecampaign.ft;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.motechproject.testing.utils.faketime.EventCaptor;
import org.motechproject.testing.utils.faketime.JvmFakeTime;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.Arrays.asList;
import static org.motechproject.util.DateUtil.newDateTime;

@ContextConfiguration(locations = "classpath:message_campaign_service_ft/context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class MessageCampaignServiceFT {

    @Autowired
    MessageCampaignService messageCampaignService;

    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;

    Scheduler scheduler;

    @Autowired
    EventListenerRegistry eventListenerRegistry;

    @Autowired
    MotechSchedulerService schedulerService;

    @Autowired
    AllCampaignEnrollments allCampaignEnrollments;

    @Before
    public void setup() {
        JvmFakeTime.load();
        //System.startFakingTime();
        scheduler = schedulerFactoryBean.getScheduler();
    }

    @After
    public void teardown() {
        schedulerService.unscheduleAllJobs("org.motechproject.server.messagecampaign");
        allCampaignEnrollments.removeAll();
    }

    @Test
    public void shouldReceiveMessageEveryWeek() {
        EventCaptor listener = new EventCaptor("listener1", scheduler);
        eventListenerRegistry.registerListener(listener, EventKeys.SEND_MESSAGE);

        CampaignRequest campaignRequest = new CampaignRequest("entity_1", "weekly_campaign", new LocalDate(2012, 10, 1), null);
        messageCampaignService.startFor(campaignRequest);

        listener.assertEventRaisedAt(asList(
            newDateTime(2012, 10, 1, 10, 30, 0),
            newDateTime(2012, 10, 8, 10, 30, 0),
            newDateTime(2012, 10, 15, 10, 30, 0)
        ));
    }
}
