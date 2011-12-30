package org.motechproject.scheduletracking.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.contract.EnrollmentRequest;
import org.motechproject.scheduletracking.api.dao.AllEnrollments;
import org.motechproject.scheduletracking.api.dao.AllTrackedSchedules;
import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testScheduleTrackingApplicationContext.xml"})
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

	@Before
	public void setup() throws SchedulerException {
		enrollmentRequest = new EnrollmentRequest("job_001", "IPTI Schedule", "sd", 1, new Time(1, 1));
		scheduler = schedulerFactoryBean.getScheduler();

		for (Enrollment enrollment : allEnrollments.getAll()) {
			allEnrollments.remove(enrollment);
		}

		for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals("default"))) {
			scheduler.deleteJob(jobKey);
		}
	}

	@Test
	public void autoWiring() {
		assertNotNull(scheduleTrackingService);
	}

	@Test
	public void shouldEnrollSchedule() throws SchedulerException {
		scheduleTrackingService.enroll(enrollmentRequest);
		assertThat(allEnrollments.getAll().size(), is(equalTo(1)));
		assertThat(scheduler.getJobKeys(GroupMatcher.jobGroupEquals("default")).size(), is(equalTo(1)));
	}

	@Test
	public void shouldNotEnrollSameExternalIdForTheSameScheduleMultipleTimes() throws SchedulerException {
		scheduleTrackingService.enroll(enrollmentRequest);
		scheduleTrackingService.enroll(enrollmentRequest);

		assertThat(allEnrollments.getAll().size(), is(equalTo(1)));
		assertThat(scheduler.getJobKeys(GroupMatcher.jobGroupEquals("default")).size(), is(equalTo(1)));
	}
}
