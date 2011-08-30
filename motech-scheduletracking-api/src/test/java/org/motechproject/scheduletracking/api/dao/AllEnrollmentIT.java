package org.motechproject.scheduletracking.api.dao;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationScheduleTrackingAPI.xml" })
public class AllEnrollmentIT {
	@Autowired
	private AllEnrollments allEnrollments;

	@Test
	public void addEnrolment() {
		Enrollment enrollment = new Enrollment("1324324", LocalDate.now(),
				"foo");
		try {
			allEnrollments.add(enrollment);
			assertNotNull(enrollment.getId());
			assertNotNull(allEnrollments.get(enrollment.getId()));
		} finally {
			allEnrollments.remove(enrollment);
		}
	}
}
