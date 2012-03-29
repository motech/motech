package org.motechproject.scheduletracking.api.domain.search;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentService;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.powermock.api.mockito.PowerMockito.when;

public class EndOfWindowCriterionTest {

    @Mock
    EnrollmentService enrollmentService;
    @Mock
    AllEnrollments allEnrollments;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldFindEnrollmentsWhoseGivenWindowEndsDuringGivenTimeRange() {
        List<Enrollment> enrollments = new ArrayList<Enrollment>();
        Enrollment enrollment1 = mock(Enrollment.class);
        Enrollment enrollment2 = mock(Enrollment.class);
        Enrollment enrollment3 = mock(Enrollment.class);
        Enrollment enrollment4 = mock(Enrollment.class);
        enrollments.addAll(asList(enrollment1, enrollment2, enrollment3, enrollment4));

        when(allEnrollments.getAll()).thenReturn(enrollments);

        when(enrollmentService.getEndOfWindowForCurrentMilestone(enrollment1, WindowName.due)).thenReturn(newDateTime(2012, 2, 3, 5, 10, 0));
        when(enrollmentService.getEndOfWindowForCurrentMilestone(enrollment2, WindowName.due)).thenReturn(newDateTime(2012, 2, 3, 0, 0, 0));
        when(enrollmentService.getEndOfWindowForCurrentMilestone(enrollment3, WindowName.due)).thenReturn(newDateTime(2012, 2, 5, 0, 0, 0));
        when(enrollmentService.getEndOfWindowForCurrentMilestone(enrollment4, WindowName.due)).thenReturn(newDateTime(2012, 2, 6, 0, 0, 0));

        DateTime start = newDateTime(2012, 2, 3, 0, 0, 0);
        DateTime end = newDateTime(2012, 2, 5, 23, 59, 59);
        List<Enrollment> fetchedEnrollments = new EndOfWindowCriterion(WindowName.due, start, end).fetch(allEnrollments, enrollmentService);
        List<Enrollment> filteredEnrollments = new EndOfWindowCriterion(WindowName.due, start, end).filter(enrollments, enrollmentService);

        assertEquals(asList(enrollment1, enrollment2, enrollment3), fetchedEnrollments);
        assertEquals(asList(enrollment1, enrollment2, enrollment3), filteredEnrollments);
    }
}
