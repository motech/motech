package org.motechproject.scheduletracking.api.domain.search;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentService;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ExternalIdCriterionTest {

    @Mock
    private AllEnrollments allEnrollments;
    @Mock
    private EnrollmentService enrollmentService;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldFetchByExternalId() {
        List<Enrollment> result = mock(List.class);
        when(allEnrollments.findByExternalId("entity1")).thenReturn(result);
        assertEquals(result, new ExternalIdCriterion("entity1").fetch(allEnrollments, enrollmentService));
    }

    @Test
    public void shouldFilterByExternalId() {
        Schedule schedule = new Schedule("some_schedule");
        List<Enrollment> allEnrollments = new ArrayList<Enrollment>();
        allEnrollments.add(new Enrollment().setExternalId("someExternalId").setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(null));
        allEnrollments.add(new Enrollment().setExternalId("someOtherExternalId").setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(null));
        allEnrollments.add(new Enrollment().setExternalId("oneMoreExternalId").setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(null));
        allEnrollments.add(new Enrollment().setExternalId("someExternalId").setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(null));

        List<Enrollment> filteredEnrollments = new ExternalIdCriterion("someExternalId").filter(allEnrollments, null);
        assertEquals(asList(new String[]{"someExternalId", "someExternalId"}), extract(filteredEnrollments, on(Enrollment.class).getExternalId()));
    }
}
