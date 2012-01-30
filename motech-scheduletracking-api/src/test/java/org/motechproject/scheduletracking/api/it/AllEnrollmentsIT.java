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
@ContextConfiguration(locations = "classpath:testApplicationSchedulerTrackingAPI.xml")
public class AllEnrollmentsIT {
	@Autowired
	private AllEnrollments allEnrollments;

	private Schedule schedule;
	private Enrollment enrollment;

	@Before
	public void setUp() {
		String milestoneName = "First Milestone";
		String scheduleName = "Schedule Name";

		Milestone milestone = new Milestone(milestoneName, new WallTime(13, WallTimeUnit.Week), new WallTime(14, WallTimeUnit.Week), new WallTime(16, WallTimeUnit.Week), null);
		schedule = new Schedule(scheduleName, new WallTime(52, WallTimeUnit.Week), milestone);
		enrollment = new Enrollment("1324324", schedule.getName(), schedule.getFirstMilestone().getName(), DateUtil.today(), DateUtil.today());
	}

	@After
	public void tearDown() {
		allEnrollments.remove(enrollment);
	}

    @Test
    public void shouldAddEnrollment() {
        allEnrollments.add(enrollment);

        String enrollmentId = enrollment.getId();
        assertNotNull(enrollmentId);
        assertNotNull(allEnrollments.get(enrollmentId));
    }

	@Test
	public void shouldFindByExternalIdAndScheduleName() {
		allEnrollments.add(enrollment);

		Enrollment found = allEnrollments.findByExternalIdAndScheduleName(enrollment.getExternalId(), schedule.getName());
		assertEquals(enrollment.getExternalId(), found.getExternalId());
        assertEquals(enrollment.getReferenceDate(), found.getReferenceDate());
	}
}
