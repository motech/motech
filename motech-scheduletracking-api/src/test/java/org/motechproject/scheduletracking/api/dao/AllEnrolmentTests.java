package org.motechproject.scheduletracking.api.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduletracking.api.domain.Enrolment;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:**/applicationScheduleTracking.xml"})
public class AllEnrolmentTests {
    @Autowired
    private AllEnrolments allEnrolments;

    @Test
    public void addEnrolment() {
        Enrolment enrolment = new Enrolment("1324324", new Date(), new WallTime(1, WallTimeUnit.Week), "");
        allEnrolments.add(enrolment);
        assertNotNull(enrolment.getId());
        Enrolment loadedEnrolment = allEnrolments.get(enrolment.getId());
        assertEquals(WallTimeUnit.Week, loadedEnrolment.getEnroledIn().getUnit());
        assertEquals(1, loadedEnrolment.getEnroledIn().getValue());
        allEnrolments.remove(enrolment);
    }
}
