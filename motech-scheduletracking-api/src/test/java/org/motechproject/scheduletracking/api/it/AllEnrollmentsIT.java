package org.motechproject.scheduletracking.api.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testScheduleTrackingApplicationContext.xml")
public class AllEnrollmentsIT {
	@Autowired
	private AllEnrollments allEnrollments;

	private Enrollment enrollment;

	@Before
	public void setUp() {
		enrollment = new Enrollment("1324324", DateUtil.today(), "Schedule Name", "First Milestone");
	}

	@After
	public void tearDown() {
		allEnrollments.remove(enrollment);
	}

	@Test
	public void shouldFindByExternalIdAndScheduleName() {
		allEnrollments.add(enrollment);

		List<Enrollment> enrollments = allEnrollments.findByExternalIdAndScheduleName(enrollment.getExternalId(), enrollment.getScheduleName());
		assertEquals(1, enrollments.size());
		Enrollment found = enrollments.get(0);
		assertEquals(enrollment.getExternalId(), found.getExternalId());
		assertEquals(enrollment.getScheduleName(), found.getScheduleName());
	}

	@Test
	public void shouldAddEnrollment() {
		allEnrollments.add(enrollment);

		String enrollmentId = enrollment.getId();
		assertNotNull(enrollmentId);
		assertNotNull(allEnrollments.get(enrollmentId));
	}

	@Test
	public void shouldAddEnrollmentWithFulfilledMilestones() {
		Milestone milestone = new Milestone("First Milestone", new WallTime(13, WallTimeUnit.Week), new WallTime(14, WallTimeUnit.Week), new WallTime(16, WallTimeUnit.Week), null);
		Schedule schedule = new Schedule("Schedule Name", new WallTime(52, WallTimeUnit.Week), milestone);
		enrollment.fulfillMilestone(schedule);
		allEnrollments.add(enrollment);

		String enrollmentId = enrollment.getId();
		assertNotNull(enrollmentId);
		assertNotNull(allEnrollments.get(enrollmentId));
	}
}
