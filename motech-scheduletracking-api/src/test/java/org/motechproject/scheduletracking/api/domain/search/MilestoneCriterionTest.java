package org.motechproject.scheduletracking.api.domain.search;

import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MilestoneCriterionTest {

    @Mock
    private AllEnrollments allEnrollments;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldFetchBySchedule() {
        List<Enrollment> enrollments = mock(List.class);
        when(allEnrollments.findByCurrentMilestone("milestone")).thenReturn(enrollments);

        assertEquals(enrollments, new MilestoneCriterion("milestone").fetch(allEnrollments, null));
    }

    @Test
    public void shouldFilterByMilestoneName() {
        Schedule schedule = new Schedule("schedule_1");
        schedule.addMilestones(new Milestone("s1_milestone-1", Period.ZERO, Period.ZERO, Period.ZERO, Period.ZERO));
        schedule.addMilestones(new Milestone("s1_milestone-2", Period.ZERO, Period.ZERO, Period.ZERO, Period.ZERO));
        schedule.addMilestones(new Milestone("s1_milestone-3", Period.ZERO, Period.ZERO, Period.ZERO, Period.ZERO));

        Schedule schedule2 = new Schedule("schedule_2");
        schedule2.addMilestones(new Milestone("s2_milestone-1", Period.ZERO, Period.ZERO, Period.ZERO, Period.ZERO));
        schedule2.addMilestones(new Milestone("s2_milestone-2", Period.ZERO, Period.ZERO, Period.ZERO, Period.ZERO));
        schedule2.addMilestones(new Milestone("s2_milestone-3", Period.ZERO, Period.ZERO, Period.ZERO, Period.ZERO));

        List<Enrollment> allEnrollments = new ArrayList<Enrollment>();
        allEnrollments.add(new Enrollment().setExternalId(null).setSchedule(schedule).setCurrentMilestoneName("s1_milestone-1").setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(null));
        allEnrollments.add(new Enrollment().setExternalId(null).setSchedule(schedule).setCurrentMilestoneName("s1_milestone-1").setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(null));
        allEnrollments.add(new Enrollment().setExternalId(null).setSchedule(schedule2).setCurrentMilestoneName("s2_milestone-2").setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(null));
        allEnrollments.add(new Enrollment().setExternalId(null).setSchedule(schedule2).setCurrentMilestoneName("s2_milestone-3").setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(null));

        List<Enrollment> filteredEnrollments = new MilestoneCriterion("s2_milestone-1").filter(allEnrollments, null);
        assertEquals(Collections.emptyList(), extract(filteredEnrollments, on(Enrollment.class).getScheduleName()));

        filteredEnrollments = new MilestoneCriterion("s2_milestone-2").filter(allEnrollments, null);
        assertEquals(asList(new String[]{"schedule_2"}), extract(filteredEnrollments, on(Enrollment.class).getScheduleName()));

        filteredEnrollments = new MilestoneCriterion("s1_milestone-1").filter(allEnrollments, null);
        assertEquals(asList(new String[]{"schedule_1", "schedule_1"}), extract(filteredEnrollments, on(Enrollment.class).getScheduleName()));
    }
}
