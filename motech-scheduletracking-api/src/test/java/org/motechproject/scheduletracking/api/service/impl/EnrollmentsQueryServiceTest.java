package org.motechproject.scheduletracking.api.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.search.Criterion;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;

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
    public void shouldFetchByPrimaryCriterionFromDbAndFilterSubsequentCriteriaInCode() {

        Criterion primaryCriterion = mock(Criterion.class);
        List<Enrollment> filteredByMetadata = mock(List.class);
        when(primaryCriterion.fetch(allEnrollments, enrollmentService)).thenReturn(filteredByMetadata);

        Criterion secondaryCriterion1 = mock(Criterion.class);
        List<Enrollment> criterion1FilteredEnrollments = mock(List.class);
        when(secondaryCriterion1.filter(filteredByMetadata, enrollmentService)).thenReturn(criterion1FilteredEnrollments);

        Criterion secondaryCriterion2 = mock(Criterion.class);
        List<Enrollment> expectedFilteredEnrollments = mock(List.class);
        when(secondaryCriterion2.filter(criterion1FilteredEnrollments, enrollmentService)).thenReturn(expectedFilteredEnrollments);

        EnrollmentsQuery enrollmentQuery = mock(EnrollmentsQuery.class);
        when(enrollmentQuery.getPrimaryCriterion()).thenReturn(primaryCriterion);
        when(enrollmentQuery.getSecondaryCriteria()).thenReturn(asList(secondaryCriterion1, secondaryCriterion2));

        assertEquals(expectedFilteredEnrollments, new EnrollmentsQueryService(allEnrollments, enrollmentService).search(enrollmentQuery));

    }
}
