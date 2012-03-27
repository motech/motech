package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.domain.exception.InvalidQueryException;
import org.motechproject.scheduletracking.api.domain.filtering.*;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.instanceOf;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.daysAgo;
import static org.springframework.util.Assert.isInstanceOf;

public class EnrollmentsQueryTest {

    EnrollmentsQuery enrollmentsQuery;

    @Before
    public void before() {
        enrollmentsQuery = new EnrollmentsQuery();
    }

    @Test
    public void shouldVerifyFindExternalIdsQuery() {
        EnrollmentsQuery query = enrollmentsQuery.havingExternalId("ext_id_1");
        List<Criterion> criteria = query.getCriteria();
        assertEquals(criteria.size(), 1);
        assertTrue(criteria.get(0) instanceof ExternalIdCriterion);
    }

    @Test
    public void shouldVerifyHavingScheduleQuery() {
        EnrollmentsQuery query = enrollmentsQuery.havingSchedule("some_schedule");
        List<Criterion> criteria = query.getCriteria();
        assertEquals(criteria.size(), 1);
        assertTrue(criteria.get(0) instanceof ScheduleCriterion);
    }

    @Test
    public void shouldVerifyhavingWindowStartingDuringQuery() {
        EnrollmentsQuery query = enrollmentsQuery.havingWindowStartingDuring(WindowName.due, DateTime.now(), daysAgo(2));
        List<Criterion> criteria = query.getCriteria();
        assertEquals(criteria.size(), 1);
        assertTrue(criteria.get(0) instanceof StartOfWindowCriterion);
    }

    @Test
    public void shouldVerifyhavingWindowEndingDuringQuery() {
        EnrollmentsQuery query = enrollmentsQuery.havingWindowEndingDuring(WindowName.due, DateTime.now(), daysAgo(2));
        List<Criterion> criteria = query.getCriteria();
        assertEquals(criteria.size(), 1);
        assertTrue(criteria.get(0) instanceof EndOfWindowCriterion);
    }

    @Test
    public void shouldVerifyCurrentlyInWindowQuery() {
        EnrollmentsQuery query = enrollmentsQuery.currentlyInWindow(WindowName.due, WindowName.earliest);
        List<Criterion> criteria = query.getCriteria();
        assertEquals(criteria.size(), 1);
        assertTrue(criteria.get(0) instanceof InWindowCriterion);
    }

    @Test
    public void shouldVerifyhavingStateQuery() {
        EnrollmentsQuery query = enrollmentsQuery.havingState("ACTIVE", "deFaulted");
        List<Criterion> criteria = query.getCriteria();
        assertEquals(criteria.size(), 1);
        assertTrue(criteria.get(0) instanceof StatusCriterion);
    }

    @Test(expected = InvalidQueryException.class)
    public void shouldThrowExceptionForInvalidState() {
        enrollmentsQuery.havingState("ACTIVE", "EaRliestzjxh");
    }

    @Test
    public void shouldBuildQueryForCompletedDuringCriterion() {
        EnrollmentsQuery query = enrollmentsQuery.completedDuring(null, null);
        List<Criterion> criteria = query.getCriteria();
        assertEquals(criteria.size(), 1);
        assertTrue(criteria.get(0) instanceof CompletedDuringCriterion);
    }

    @Test
    public void shouldReturnPrimaryCriterion() {
        EnrollmentsQuery query = enrollmentsQuery.havingExternalId("entity1").havingState("active");
        isInstanceOf(ExternalIdCriterion.class, enrollmentsQuery.getPrimaryCriterion());
    }

    @Test
    public void shouldReturnSecondaryCriteria() {
        EnrollmentsQuery query = enrollmentsQuery.havingExternalId("entity1").havingState("active").completedDuring(null, null);
        List<Criterion> secondaryCriteria = enrollmentsQuery.getSecondaryCriteria();
        isInstanceOf(StatusCriterion.class, secondaryCriteria.get(0));
        isInstanceOf(CompletedDuringCriterion.class, secondaryCriteria.get(1));
    }
}
