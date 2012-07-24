package org.motechproject.scheduletracking.api.domain.search;

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
        when(allEnrollments.findBySchedule(asList(new String[]{ "schedule1", "schedule2" }))).thenReturn(enrollments);

        assertEquals(enrollments, new ScheduleCriterion("schedule1", "schedule2").fetch(allEnrollments, null));
    }

    @Test
    public void shouldFilterByScheduleNames() {
        Schedule schedule = new Schedule("schedule_1");
        Schedule schedule2 = new Schedule("schedule_2");
        Schedule schedule3 = new Schedule("schedule_3");
        List<Enrollment> allEnrollments = new ArrayList<Enrollment>();
        allEnrollments.add(new Enrollment().setExternalId(null).setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(null));
        allEnrollments.add(new Enrollment().setExternalId(null).setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(null));
        allEnrollments.add(new Enrollment().setExternalId(null).setSchedule(schedule2).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(null));
        allEnrollments.add(new Enrollment().setExternalId(null).setSchedule(schedule2).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(null));
        allEnrollments.add(new Enrollment().setExternalId(null).setSchedule(schedule3).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(null));

        List<Enrollment> filteredEnrollments = new ScheduleCriterion("schedule_1", "schedule_3").filter(allEnrollments, null);
        assertEquals(asList(new String[]{"schedule_1", "schedule_1", "schedule_3"}), extract(filteredEnrollments, on(Enrollment.class).getScheduleName()));
    }
}
