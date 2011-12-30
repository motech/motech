package org.motechproject.server.messagecampaign.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerServiceImpl;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.util.DateUtil;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testMessageCampaignApplicationContext.xml"})
public class MessageCampaignServiceIT {

	@Autowired
	private MessageCampaignService messageCampaignService;
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Test
	public void testEnrollForAbsoluteProgram() throws Exception {
		int scheduledJobsNum = getNumberOfScheduledJobs();

		CampaignRequest enrollRequest = new CampaignRequest();
		enrollRequest.setCampaignName("Absolute Dates Message Program");
		enrollRequest.setExternalId("patient_Id1");
		enrollRequest.setReminderTime(new Time(9, 30));
		messageCampaignService.startFor(enrollRequest);
		assertEquals(scheduledJobsNum + 2, getNumberOfScheduledJobs());
	}

	@Test
	public void testEnrollForOffsetProgram() throws Exception {
		int scheduledJobsNum = getNumberOfScheduledJobs();

		CampaignRequest enrollRequest = new CampaignRequest();
		enrollRequest.setCampaignName("Relative Dates Message Program");
		enrollRequest.setExternalId("patient_Id2");
		enrollRequest.setReferenceDate(DateUtil.today().plusDays(1));
		enrollRequest.setReminderTime(new Time(9, 30));
		messageCampaignService.startFor(enrollRequest);
		assertEquals(scheduledJobsNum + 3, getNumberOfScheduledJobs());
	}

	@Test
	public void testEnrollForRepeatingProgram() throws Exception {
		int scheduledJobsNum = getNumberOfScheduledJobs();

		CampaignRequest enrollRequest = new CampaignRequest();
		enrollRequest.setCampaignName("Relative Parameterized Dates Message Program");
		enrollRequest.setExternalId("patiend_Id3");
		enrollRequest.setReferenceDate(DateUtil.today().plusDays(1));
		enrollRequest.setReminderTime(new Time(9, 30));
		messageCampaignService.startFor(enrollRequest);
		assertEquals(scheduledJobsNum + 5, getNumberOfScheduledJobs());
	}

	@Test
	public void testEnrollForCronBasedProgram() throws Exception {
		int scheduledJobsNum = getNumberOfScheduledJobs();

		CampaignRequest enrollRequest = new CampaignRequest();
		enrollRequest.setCampaignName("Cron based Message Program");
		enrollRequest.setExternalId("patiend_Id3");
		enrollRequest.setReferenceDate(DateUtil.today());
		messageCampaignService.startFor(enrollRequest);
		assertEquals(scheduledJobsNum + 1, getNumberOfScheduledJobs());
	}

	private int getNumberOfScheduledJobs() throws SchedulerException {
		return schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME)).size();
	}
}
