package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EnrollmentRequestTest {
    @Test
    public void shouldReturnFalseIfStartingMilestoneNotProvided() {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest("externalId", "scheduleName", new Time(10, 10), LocalDate.now(), null, null);
        assertFalse("Starting milestone not expected, but was provided!", enrollmentRequest.enrollIntoMilestone());
    }

    @Test
    public void shouldReturnTrueIfStartingMilestoneProvided() {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest("externalId", "scheduleName", new Time(10, 10), LocalDate.now(), null, "Milestone");
        assertTrue("Starting milestone expected, but was not provided!", enrollmentRequest.enrollIntoMilestone());
    }

    @Test
    public void shouldReturnFalseIfEmptyStartingMilestoneProvided() {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest("externalId", "scheduleName", new Time(10, 10), LocalDate.now(), null, "");
        assertFalse("Starting milestone not expected, but was provided!", enrollmentRequest.enrollIntoMilestone());
    }

    @Test
    public void shouldReturnFalseIfNullStartingMilestoneProvided() {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest("externalId", "scheduleName", new Time(10, 10), LocalDate.now(), null, null);
        assertFalse("Starting milestone not expected, but was provided!", enrollmentRequest.enrollIntoMilestone());
    }

    @Test
    public void shouldReturnTodayIfEnrollmentDateIsNotProvided() {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest("externalId", "scheduleName", new Time(10, 10), LocalDate.now(), null, null);
        assertEquals(DateUtil.today(), enrollmentRequest.enrollmentDate());
    }

    @Test
    public void shouldReturnEnrollmentDateIfProvided() {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest("externalId", "scheduleName", new Time(10, 10), LocalDate.now(), DateUtil.newDate(2012, 12, 10), null);
        assertEquals(DateUtil.newDate(2012, 12, 10), enrollmentRequest.enrollmentDate());
    }
}
