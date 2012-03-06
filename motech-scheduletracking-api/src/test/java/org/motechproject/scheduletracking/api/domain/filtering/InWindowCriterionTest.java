package org.motechproject.scheduletracking.api.domain.filtering;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentService;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class InWindowCriterionTest {

    @Mock
    EnrollmentService enrollmentService;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldFilterByWindowName() {
        List<Enrollment> allEnrollments = new ArrayList<Enrollment>();
        Enrollment enrollment1 = mock(Enrollment.class);
        Enrollment enrollment2 = mock(Enrollment.class);
        Enrollment enrollment3 = mock(Enrollment.class);
        allEnrollments.addAll(asList(enrollment1, enrollment2, enrollment3));

        when(enrollmentService.getCurrentWindowAsOf(eq(enrollment1), Matchers.<DateTime>any())).thenReturn(WindowName.earliest);
        when(enrollmentService.getCurrentWindowAsOf(eq(enrollment2), Matchers.<DateTime>any())).thenReturn(WindowName.due);
        when(enrollmentService.getCurrentWindowAsOf(eq(enrollment3), Matchers.<DateTime>any())).thenReturn(WindowName.earliest);

        assertEquals(asList(enrollment1, enrollment3), new InWindowCriterion((asList(new WindowName[]{WindowName.earliest})), enrollmentService).filter(allEnrollments));
        assertEquals(asList(enrollment2), new InWindowCriterion((asList(new WindowName[]{WindowName.due})), enrollmentService).filter(allEnrollments));
    }

    @Test
    public void shouldFilterPerformingAnOrOperationOverTheValues() {
        List<Enrollment> allEnrollments = new ArrayList<Enrollment>();
        Enrollment enrollment1 = mock(Enrollment.class);
        Enrollment enrollment2 = mock(Enrollment.class);
        Enrollment enrollment3 = mock(Enrollment.class);
        allEnrollments.addAll(asList(enrollment1, enrollment2, enrollment3));

        when(enrollmentService.getCurrentWindowAsOf(eq(enrollment1), Matchers.<DateTime>any())).thenReturn(WindowName.earliest);
        when(enrollmentService.getCurrentWindowAsOf(eq(enrollment2), Matchers.<DateTime>any())).thenReturn(WindowName.due);
        when(enrollmentService.getCurrentWindowAsOf(eq(enrollment3), Matchers.<DateTime>any())).thenReturn(WindowName.late);

        assertEquals(asList(enrollment1, enrollment3), new InWindowCriterion((asList(new WindowName[]{WindowName.earliest, WindowName.late})), enrollmentService).filter(allEnrollments));
    }
}
