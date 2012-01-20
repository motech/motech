package org.motechproject.scheduletracking.api.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.motechproject.util.DateUtil.newDate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testApplicationSchedulerTrackingAPI.xml")
public class ScheduleTrackingServiceIT {
	@Autowired
	private ScheduleTrackingService scheduleTrackingService;
	@Autowired
	private AllEnrollments allEnrollments;
	@Autowired
	private AllTrackedSchedules allTrackedSchedules;
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	private EnrollmentRequest enrollmentRequest;
	private Scheduler scheduler;
	private static final String GROUP_NAME = "default";

	@Before
	public void setup() throws SchedulerException {
		enrollmentRequest = new EnrollmentRequest("job_001", "IPTI Schedule", new Time(1, 1), newDate(2012, 1, 2));
		scheduler = schedulerFactoryBean.getScheduler();
	}

	@After
	public void tearDown() throws SchedulerException {
		for (Enrollment enrollment : allEnrollments.getAll()) {
			allEnrollments.remove(enrollment);
		}

		for (String jobName : scheduler.getJobNames(GROUP_NAME)) {
			scheduler.deleteJob(jobName, GROUP_NAME);
		}
	}

	@Test
	public void shouldEnrollSchedule() throws SchedulerException {
		scheduleTrackingService.enroll(enrollmentRequest);

		assertEquals(1, allEnrollments.getAll().size());
		assertEquals(1, scheduler.getJobNames(GROUP_NAME).length);
	}

	@Test
	public void shouldUseTheCorrectStartDateAndEndDateForTheSchedule() throws SchedulerException {
		scheduleTrackingService.enroll(enrollmentRequest);

		String[] triggerNames = scheduler.getTriggerNames(GROUP_NAME);
		Trigger trigger = scheduler.getTrigger(triggerNames[0], GROUP_NAME);

		assertEquals(newDate(2012, 1, 2).toDate(), trigger.getStartTime());
		assertEquals(newDate(2012, 12, 31).toDate(), trigger.getEndTime());
	}

	@Test
	public void shouldNotEnrollSameExternalIdForTheSameScheduleMultipleTimes() throws SchedulerException {
		scheduleTrackingService.enroll(enrollmentRequest);
		scheduleTrackingService.enroll(enrollmentRequest);

		assertEquals(1, allEnrollments.getAll().size());
		assertEquals(1, scheduler.getJobNames(GROUP_NAME).length);
	}
}
