package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.motechproject.util.DateUtil.*;

public class EnrollmentRequestTest {
    
    @Test
    public void shouldReturnFalseIfStartingMilestoneNotProvided() {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest("externalId", "scheduleName", new Time(10, 10), LocalDate.now(), null, null, null, null, null);
        assertFalse("Starting milestone not expected, but was provided!", enrollmentRequest.isStartingMilestoneSpecified());
    }

    @Test
    public void shouldReturnTrueIfStartingMilestoneProvided() {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest("externalId", "scheduleName", new Time(10, 10), LocalDate.now(), null, null, null, "Milestone", null);
        assertTrue("Starting milestone expected, but was not provided!", enrollmentRequest.isStartingMilestoneSpecified());
    }

    @Test
    public void shouldReturnFalseIfEmptyStartingMilestoneProvided() {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest("externalId", "scheduleName", new Time(10, 10), LocalDate.now(), null, null, null, "", null);
        assertFalse("Starting milestone not expected, but was provided!", enrollmentRequest.isStartingMilestoneSpecified());
    }

    @Test
    public void shouldReturnFalseIfNullStartingMilestoneProvided() {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest("externalId", "scheduleName", new Time(10, 10), LocalDate.now(), null, null, null, null, null);
        assertFalse("Starting milestone not expected, but was provided!", enrollmentRequest.isStartingMilestoneSpecified());
    }

    @Test
    public void enrollmentDateShouldBeTodayAndEnrollmentTimeShouldBeMidnightWhenNotDefined() {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest("externalId", "scheduleName", new Time(10, 10), null, null, null, null, null, null);
        assertEquals(newDateTime(today()), enrollmentRequest.getEnrollmentDateTime());
    }

    @Test
    public void shouldReturnEnrollmentDateTimeWithGivenDateAndTime() {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest("externalId", "scheduleName", new Time(10, 10), null, null, newDate(2012, 3, 22), new Time(8, 30), null, null);
        assertEquals(newDateTime(2012, 3, 22, 8, 30, 0), enrollmentRequest.getEnrollmentDateTime());
    }

    @Test
    public void referenceDateShouldBeTodayAndReferenceTimeShouldBeMidnightWhenNotDefined() {
        EnrollmentRequest referenceRequest = new EnrollmentRequest("externalId", "scheduleName", new Time(10, 10), null, null, null, null, null, null);
        assertEquals(newDateTime(today()), referenceRequest.getReferenceDateTime());
    }

    @Test
    public void shouldReturnReferenceDateTimeWithGivenDateAndTime() {
        EnrollmentRequest referenceRequest = new EnrollmentRequest("externalId", "scheduleName", new Time(10, 10), newDate(2012, 3, 22), new Time(8, 30), null, null, null, null);
        assertEquals(newDateTime(2012, 3, 22, 8, 30, 0), referenceRequest.getReferenceDateTime());
    }
}
