package org.motechproject.server.messagecampaign.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduler.MotechSchedulerServiceImpl;
import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationMessageCampaign.xml"})
public class MessageCampaignServiceIT {

    @Autowired
    private MessageCampaignService messageCampaignService;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Test
    public void testEnrollWithAbsolute() throws Exception {
        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        EnrollRequest enrollRequest = new EnrollRequest();
        enrollRequest.campaignName("Absolute Dates Message Program");
        enrollRequest.externalId("patiend Id");
        messageCampaignService.enroll(enrollRequest);
        assertEquals(scheduledJobsNum + 2, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);
    }
}
