package org.motechproject.server.messagecampaign.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerServiceImpl;
import org.motechproject.server.messagecampaign.contract.EnrollForAbsoluteProgramRequest;
import org.motechproject.server.messagecampaign.contract.EnrollForRelativeProgramRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationMessageCampaign.xml"})
public class MessageCampaignServiceIT {

    @Autowired
    private MessageCampaignService messageCampaignService;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Test
    public void testEnrollWithAbsoluteProgram() throws Exception {
        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        EnrollForAbsoluteProgramRequest enrollRequest = new EnrollForAbsoluteProgramRequest();
        enrollRequest.campaignName("Absolute Dates Message Program");
        enrollRequest.externalId("patiend Id");
        enrollRequest.reminderTime(new Time(9, 30));
        messageCampaignService.enroll(enrollRequest);
        assertEquals(scheduledJobsNum + 2, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);
    }

    @Test
    public void testEnrollWithSimpleRelativeProgram() throws Exception {
        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        EnrollForRelativeProgramRequest enrollRequest = new EnrollForRelativeProgramRequest();
        enrollRequest.campaignName("Weekly Info Child Program");
        enrollRequest.externalId("patiend Id");
        enrollRequest.referenceDate(Calendar.getInstance().getTime());
        enrollRequest.reminderTime(new Time(9, 30));
        messageCampaignService.enroll(enrollRequest);
        assertEquals(scheduledJobsNum + 3, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);
    }
}
