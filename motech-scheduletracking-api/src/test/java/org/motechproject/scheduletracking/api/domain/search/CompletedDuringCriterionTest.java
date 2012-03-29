package org.motechproject.scheduletracking.api.domain.search;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.newDateTime;

public class CompletedDuringCriterionTest {

    @Mock
    private AllEnrollments allEnrollments;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldFetchEnrollmentsCompletedDuringTheGivenTimeRangeFromTheDb() {
        DateTime start = newDateTime(2012, 1, 2, 0, 0, 0);
        DateTime end = newDateTime(2012, 1, 3, 0, 0, 0);
        List<Enrollment> enrollments = mock(List.class);
        when(allEnrollments.completedDuring(start,  end)).thenReturn(enrollments);

        assertEquals(enrollments, new CompletedDuringCriterion(start, end).fetch(allEnrollments, null));
    }

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
