package org.motechproject.scheduletracking.api.domain.filtering;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ScheduleCriterionTest {

    @Mock
    private AllEnrollments allEnrollments;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldFetchBySchedule() {
        List<Enrollment> enrollments = mock(List.class);
        when(allEnrollments.findBySchedule("schedule")).thenReturn(enrollments);

        assertEquals(enrollments, new ScheduleCriterion("schedule").fetch(allEnrollments, null));
    }

    @Test
    public void shouldFilterByExternalId() {
        Schedule schedule = new Schedule("schedule_1");
        Schedule schedule2 = new Schedule("schedule_2");
        List<Enrollment> allEnrollments = new ArrayList<Enrollment>();
        allEnrollments.add(new Enrollment(null, schedule, null, null, null, null, null, null));
        allEnrollments.add(new Enrollment(null, schedule, null, null, null, null, null, null));
        allEnrollments.add(new Enrollment(null, schedule2, null, null, null, null, null, null));
        allEnrollments.add(new Enrollment(null, schedule2, null, null, null, null, null, null));

        List<Enrollment> filteredEnrollments = new ScheduleCriterion("schedule_1").filter(allEnrollments, null);
        assertEquals(asList(new String[]{"schedule_1", "schedule_1"}), extract(filteredEnrollments, on(Enrollment.class).getScheduleName()));
    }
}
