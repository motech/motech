package org.motechproject.server.messagecampaign.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerServiceImpl;
import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
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
    public void testEnrollForAbsoluteProgram() throws Exception {
        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        EnrollRequest enrollRequest = new EnrollRequest();
        enrollRequest.campaignName("Absolute Dates Message Program");
        enrollRequest.externalId("patient_Id1");
        enrollRequest.reminderTime(new Time(9, 30));
        messageCampaignService.enroll(enrollRequest);
        assertEquals(scheduledJobsNum + 2, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);
    }

    @Test
    public void testEnrollForOffsetProgram() throws Exception {
        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        EnrollRequest enrollRequest = new EnrollRequest();
        enrollRequest.campaignName("Relative Dates Message Program");
        enrollRequest.externalId("patient_Id2");
        enrollRequest.referenceDate(Calendar.getInstance().getTime());
        enrollRequest.reminderTime(new Time(9, 30));
        messageCampaignService.enroll(enrollRequest);
        assertEquals(scheduledJobsNum + 3, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);
    }

    @Test
    public void testEnrollForRepeatingProgram() throws Exception {
        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        EnrollRequest enrollRequest = new EnrollRequest();
        enrollRequest.campaignName("Relative Parameterized Dates Message Program");
        enrollRequest.externalId("patiend_Id3");
        enrollRequest.referenceDate(Calendar.getInstance().getTime());
        enrollRequest.reminderTime(new Time(9, 30));
        messageCampaignService.enroll(enrollRequest);
        assertEquals(scheduledJobsNum + 12, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);
    }

    @Test
    public void testEnrollForCronBasedProgram() throws Exception {
        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        EnrollRequest enrollRequest = new EnrollRequest();
        enrollRequest.campaignName("Cron based Message Program");
        enrollRequest.externalId("patiend_Id3");
        enrollRequest.referenceDate(Calendar.getInstance().getTime());
        messageCampaignService.enroll(enrollRequest);
        assertEquals(scheduledJobsNum + 1, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);
    }
}
