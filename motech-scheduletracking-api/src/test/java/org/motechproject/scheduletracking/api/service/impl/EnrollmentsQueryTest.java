package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.domain.search.*;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.daysAgo;
import static org.springframework.util.Assert.isInstanceOf;

public class EnrollmentsQueryTest {
    private EnrollmentsQuery enrollmentsQuery;

    @Before
    public void setUp() {
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
    public void shouldVerifyHavingCurrentMilestoneQuery() {
        EnrollmentsQuery query = enrollmentsQuery.havingCurrentMilestone("some_milestone");
        List<Criterion> criteria = query.getCriteria();
        assertEquals(criteria.size(), 1);
        assertTrue(criteria.get(0) instanceof MilestoneCriterion);
    }

    @Test
    public void shouldVerifyHavingWindowStartingDuringQuery() {
        EnrollmentsQuery query = enrollmentsQuery.havingWindowStartingDuring(WindowName.due, DateTime.now(), daysAgo(2));
        List<Criterion> criteria = query.getCriteria();
        assertEquals(criteria.size(), 1);
        assertTrue(criteria.get(0) instanceof StartOfWindowCriterion);
    }

    @Test
    public void shouldVerifyHavingWindowEndingDuringQuery() {
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
    public void shouldVerifyHavingStateQuery() {
        EnrollmentsQuery query = enrollmentsQuery.havingState(EnrollmentStatus.ACTIVE);
        List<Criterion> criteria = query.getCriteria();
        assertEquals(criteria.size(), 1);
        assertTrue(criteria.get(0) instanceof StatusCriterion);
    }

    @Test
    public void shouldHaveMetaDataCriterion() {
        EnrollmentsQuery query = enrollmentsQuery.havingMetadata("metaData", "value");
        List<Criterion> criteria = query.getCriteria();
        assertEquals(criteria.size(), 1);
        assertTrue(criteria.get(0) instanceof MetadataCriterion);
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
        enrollmentsQuery.havingExternalId("entity1").havingState(EnrollmentStatus.ACTIVE);
        isInstanceOf(ExternalIdCriterion.class, enrollmentsQuery.getPrimaryCriterion());
    }

    @Test
    public void shouldReturnSecondaryCriteria() {
        enrollmentsQuery.havingExternalId("entity1").havingState(EnrollmentStatus.ACTIVE).completedDuring(null, null);
        List<Criterion> secondaryCriteria = enrollmentsQuery.getSecondaryCriteria();
        isInstanceOf(StatusCriterion.class, secondaryCriteria.get(0));
        isInstanceOf(CompletedDuringCriterion.class, secondaryCriteria.get(1));
    }
}
