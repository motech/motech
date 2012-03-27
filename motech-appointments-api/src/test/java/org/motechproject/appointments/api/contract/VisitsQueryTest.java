package org.motechproject.appointments.api.contract;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.appointments.api.model.Criterion;
import org.motechproject.appointments.api.model.DueDateInCriterion;
import org.motechproject.appointments.api.model.UnvisitedCriterion;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class VisitsQueryTest {

    VisitsQuery visitsQuery;

    @Before
    public void before() {
        visitsQuery = new VisitsQuery();
    }

    @Test
    public void shouldQueryForVisitsWithDueDateInRange() {
        VisitsQuery query = visitsQuery.withDueDateIn(null, null);
        List<Criterion> criteria = query.getCriteria();
        assertEquals(criteria.size(), 1);
        assertTrue(criteria.get(0) instanceof DueDateInCriterion);
    }

    @Test
    public void shouldQueryForUnvisitedVisits() {
        VisitsQuery query = visitsQuery.unvisited();
        List<Criterion> criteria = query.getCriteria();
        assertEquals(criteria.size(), 1);
        assertTrue(criteria.get(0) instanceof UnvisitedCriterion);
    }

}
