package org.motechproject.scheduletracking.api.domain.filtering;

import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.Enrollment;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

public class ExternalIdCriterionTest {

    @Test
    public void shouldFilterByExternalId() {
        List<Enrollment> allEnrollments = new ArrayList<Enrollment>();
        allEnrollments.add(new Enrollment("someExternalId", null, null, null, null, null, null));
        allEnrollments.add(new Enrollment("someOtherExternalId", null, null, null, null, null, null));
        allEnrollments.add(new Enrollment("oneMoreExternalId", null, null, null, null, null, null));
        allEnrollments.add(new Enrollment("someExternalId", null, null, null, null, null, null));

        List<Enrollment> filteredEnrollments = new ExternalIdCriterion("someExternalId").filter(allEnrollments, null);
        assertEquals(asList(new String[]{"someExternalId", "someExternalId"}), extract(filteredEnrollments, on(Enrollment.class).getExternalId()));
    }
}
