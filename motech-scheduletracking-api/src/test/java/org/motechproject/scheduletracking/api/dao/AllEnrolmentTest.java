package org.motechproject.scheduletracking.api.dao;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduletracking.api.domain.enrolment.Enrolment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationScheduleTrackingAPI.xml"})
public class AllEnrolmentTest {
    @Autowired
    private AllEnrolments allEnrolments;

    @Test
    public void addEnrolment() {
        Enrolment enrolment = new Enrolment("1324324", LocalDate.now(), "foo");
        try {
            allEnrolments.add(enrolment);
            assertNotNull(enrolment.getId());
            assertNotNull(allEnrolments.get(enrolment.getId()));
        } finally {
            allEnrolments.remove(enrolment);
        }
    }
}
