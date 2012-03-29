package org.motechproject.scheduletracking.api.domain.search;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Metadata;
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

public class MetadataPropertyCriterionTest {

    @Mock
    private AllEnrollments allEnrollments;
    @Mock
    private EnrollmentService enrollmentService;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldFetchFromDbUsingCriteria() {
        List<Enrollment> result = mock(List.class);
        when(allEnrollments.findByMetadataProperty("foo", "bar")).thenReturn(result);
        assertEquals(result, new MetadataCriterion("foo", "bar").fetch(allEnrollments, enrollmentService));
    }

    @Test
    public void shouldFilter() {
        Schedule schedule = new Schedule("my_schedule");
        List<Enrollment> enrollments = new ArrayList<Enrollment>();
        enrollments.add(new Enrollment("entity1", schedule, null, null, null, null, null, asList(new Metadata[]{new Metadata("foo", "bar"), new Metadata("fuu", "bar")})));
        enrollments.add(new Enrollment("entity2", schedule, null, null, null, null, null, asList(new Metadata[]{new Metadata("foo", "baz"), new Metadata("fuu", "biz")})));
        enrollments.add(new Enrollment("entity3", schedule, null, null, null, null, null, asList(new Metadata[]{new Metadata("foo", "bar")})));
        enrollments.add(new Enrollment("entity4", schedule, null, null, null, null, null, asList(new Metadata[]{new Metadata("foo", "boz"), new Metadata("fuu", "ber")})));

        List<Enrollment> filtered = new MetadataCriterion("foo", "bar").filter(enrollments, null);
        assertEquals(asList(new String[]{ "entity1", "entity3" }), extract(filtered, on(Enrollment.class).getExternalId()));
    }
}
