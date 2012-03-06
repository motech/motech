package org.motechproject.scheduletracking.api.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.filtering.Criterion;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EnrollmentsQueryServiceTest {

    @Mock
    private EnrollmentService enrollmentService;

    @Mock
    private AllEnrollments allEnrollments;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldFilterEnrollmentsBasedOnTheQuery() {
        List<Criterion> criteria = new ArrayList<Criterion>();
        Criterion criterion1 = mock(Criterion.class);
        Criterion criterion2 = mock(Criterion.class);
        criteria.addAll(asList(new Criterion[]{ criterion1, criterion2 }));

        EnrollmentsQuery enrollmentQuery = mock(EnrollmentsQuery.class);
        when(enrollmentQuery.getCriteria()).thenReturn(criteria);

        List<Enrollment> enrollments = mock(List.class);
        when(allEnrollments.getAll()).thenReturn(enrollments);
        List<Enrollment> criterion1FilteredEnrollments = mock(List.class);
        when(criterion1.filter(enrollments, enrollmentService)).thenReturn(criterion1FilteredEnrollments);
        List<Enrollment> expectedFilteredEnrollments = mock(List.class);;
        when(criterion2.filter(criterion1FilteredEnrollments, enrollmentService)).thenReturn(expectedFilteredEnrollments);

        assertEquals(expectedFilteredEnrollments, new EnrollmentsQueryService(enrollmentService, allEnrollments).search(enrollmentQuery));
    }
}
