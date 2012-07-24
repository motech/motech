package org.motechproject.scheduletracking.api.domain.search;

import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
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

public class StatusCriterionTest {

    @Test
    public void shouldFilterByExternalId() {
        Schedule schedule = new Schedule("some_schedule");
        List<Enrollment> allEnrollments = new ArrayList<Enrollment>();
        allEnrollments.add(new Enrollment().setExternalId(null).setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(EnrollmentStatus.COMPLETED).setMetadata(null));
        allEnrollments.add(new Enrollment().setExternalId(null).setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null));
        allEnrollments.add(new Enrollment().setExternalId(null).setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(EnrollmentStatus.DEFAULTED).setMetadata(null));
        allEnrollments.add(new Enrollment().setExternalId(null).setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null));

        List<Enrollment> filteredEnrollments = new StatusCriterion(EnrollmentStatus.ACTIVE).filter(allEnrollments, null);
        assertEquals(asList(new EnrollmentStatus[]{ EnrollmentStatus.ACTIVE, EnrollmentStatus.ACTIVE}), extract(filteredEnrollments, on(Enrollment.class).getStatus()));
    }

    @Test
    public void shouldFetchByExternalIdFromDb() {
        List<Enrollment> enrollments = mock(List.class);
        AllEnrollments allEnrollments = mock(AllEnrollments.class);

        when(allEnrollments.findByStatus(EnrollmentStatus.ACTIVE)).thenReturn(enrollments);

        assertEquals(enrollments, new StatusCriterion(EnrollmentStatus.ACTIVE).fetch(allEnrollments,null));
    }

}
