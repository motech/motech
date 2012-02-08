package org.motechproject.scheduletracking.api.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testApplicationSchedulerTrackingAPI.xml")
public class AllEnrollmentsIT {
    @Autowired
    private AllEnrollments allEnrollments;

    private Enrollment enrollment;
    private Milestone milestone;
    private Schedule schedule;

    @Before
    public void setUp() {
        milestone = new Milestone("first_milestone", new WallTime(13, WallTimeUnit.Week), new WallTime(14, WallTimeUnit.Week), new WallTime(16, WallTimeUnit.Week), null);
        schedule = new Schedule("schedule_name");
        schedule.addMilestones(milestone);
    }

    @After
    public void tearDown() {
        allEnrollments.remove(enrollment);
    }

    @Test
    public void shouldAddEnrollment() {
        enrollment = new Enrollment("externalId", "schedule_name", "first_milestone", DateUtil.today(), DateUtil.today(), new Time(DateUtil.now().toLocalTime()));
        allEnrollments.add(enrollment);

        enrollment = allEnrollments.get(enrollment.getId());
        assertNotNull(enrollment);
        assertEquals(EnrollmentStatus.Active, enrollment.getStatus());
    }

    @Test
    public void shouldFindActiveEnrollmentByExternalIdAndScheduleName() {
        enrollment = new Enrollment("entity_1", "schedule_name", "first_milestone", DateUtil.today(), DateUtil.today(), new Time(DateUtil.now().toLocalTime()));
        enrollment.setStatus(EnrollmentStatus.Unenrolled);
        allEnrollments.add(enrollment);

        assertNull(allEnrollments.findActiveByExternalIdAndScheduleName("entity_1", "schedule_name"));
    }

    @Test
    public void shouldUpdateEnrollmentIfAnActiveEnrollmentAlreadyAvailable() {
        String externalId = "externalId";
        enrollment = new Enrollment(externalId, schedule.getName(), milestone.getName(), DateUtil.today(), DateUtil.today(), new Time(8, 10));
        allEnrollments.add(enrollment);

        Enrollment enrollmentWithUpdates = new Enrollment(enrollment.getExternalId(), enrollment.getScheduleName(), milestone.getName(), enrollment.getReferenceDate().plusDays(1), enrollment.getEnrollmentDate().plusDays(1), new Time(2, 5));
        allEnrollments.addOrReplace(enrollmentWithUpdates);

        enrollment = allEnrollments.findActiveByExternalIdAndScheduleName(enrollment.getExternalId(), schedule.getName());
        assertEquals(enrollmentWithUpdates.getCurrentMilestoneName(), enrollment.getCurrentMilestoneName());
        assertEquals(enrollmentWithUpdates.getReferenceDate(), enrollment.getReferenceDate());
        assertEquals(enrollmentWithUpdates.getEnrollmentDate(), enrollment.getEnrollmentDate());
        assertEquals(enrollmentWithUpdates.getPreferredAlertTime(), enrollment.getPreferredAlertTime());
    }
}
