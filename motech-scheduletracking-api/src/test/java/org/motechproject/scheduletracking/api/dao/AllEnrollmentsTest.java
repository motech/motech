package org.motechproject.scheduletracking.api.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.contract.EnrollmentRequest;
import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;
import org.motechproject.scheduletracking.api.domain.factory.EnrollmentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationScheduleTrackingAPI.xml", "classpath*:applicationPlatformScheduler.xml"})
public class AllEnrollmentsTest {
    @Autowired
    private AllEnrollments allEnrollments;
    private Enrollment enrollment;

    @Before
    public void setUp() {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest("123", "schedule-1", "milestone-1", 12, new Time(1, 1));
        enrollment = EnrollmentFactory.newEnrolment(enrollmentRequest);
        allEnrollments.add(enrollment);
    }

    @Test
    public void shouldFindByExternalIdAndScheduleName() throws Exception {
        List<Enrollment> enrollments = allEnrollments.findByExternalIdAndScheduleName(enrollment.getExternalId(), enrollment.getScheduleName());
        assertThat(enrollments.size(), is(equalTo(1)));
        Enrollment found = enrollments.get(0);
        assertThat(found.getExternalId(), is(equalTo(enrollment.getExternalId())));
        assertThat(found.getScheduleName(), is(equalTo(enrollment.getScheduleName())));
    }

    @After
    public void tearDown() {
        allEnrollments.remove(enrollment);
    }
}
