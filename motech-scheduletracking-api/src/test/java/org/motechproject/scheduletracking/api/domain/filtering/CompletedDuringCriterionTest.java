package org.motechproject.scheduletracking.api.domain.filtering;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.Enrollment;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.motechproject.util.DateUtil.newDateTime;

public class CompletedDuringCriterionTest {

    @Test
    public void shouldReturnEnrollmentsThatWereCompletedDuringGivenPeriod() {
        List<Enrollment> allEnrollments = new ArrayList<Enrollment>();
        Enrollment enrollment1 = mockEnrollment(true, newDateTime(2011, 12, 15, 0, 0, 0));
        Enrollment enrollment2 = mockEnrollment(true, newDateTime(2011, 12, 11, 0, 0, 0));
        Enrollment enrollment3 = mockEnrollment(false, newDateTime(2011, 12, 16, 0, 0, 0));
        Enrollment enrollment4 = mockEnrollment(false, null);
        allEnrollments.addAll(asList(enrollment1, enrollment2, enrollment3, enrollment4));

        List<Enrollment> filteredEnrollments = new CompletedDuringCriterion(newDateTime(2011, 12, 15, 0, 0, 0), newDateTime(2011, 12, 16, 0, 0, 0)).filter(allEnrollments, null);
        assertEquals(asList(enrollment1), filteredEnrollments);
    }

    private Enrollment mockEnrollment(boolean isCompleted, DateTime lastFulfilledDate) {
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getLastFulfilledDate()).thenReturn(lastFulfilledDate);
        when(enrollment.isCompleted()).thenReturn(isCompleted);
        return enrollment;
    }
}
