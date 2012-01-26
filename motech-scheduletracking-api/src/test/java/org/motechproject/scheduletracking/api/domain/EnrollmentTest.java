package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.wallTimeOf;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.weeksAgo;

public class EnrollmentTest {
    private Enrollment enrollment;
    private Milestone firstMilestone;
    private Milestone secondMilestone;
    private Schedule schedule;
    private LocalDate referenceDate;

    @Before
    public void setUp() {
        secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        firstMilestone = new Milestone("First Shot", secondMilestone, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        schedule = new Schedule("Yellow Fever Vaccination", wallTimeOf(52), firstMilestone);
        referenceDate = weeksAgo(5);
        enrollment = new Enrollment("ID-074285", schedule.getName(), weeksAgo(3), referenceDate, schedule.getFirstMilestone().getName());
    }

    @Test
    public void shouldStartWithFirstMilestoneByDefault() {
        assertEquals(firstMilestone.getName(), enrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldStartWithSecondMilestone() {
        Enrollment lateEnrollment = new Enrollment("my_entity_1", schedule.getName(), weeksAgo(3), weeksAgo(3), secondMilestone.getName());
        assertEquals(secondMilestone.getName(), lateEnrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldMarkAMilestoneAsFulfilled() {
        enrollment.fulfillMilestone(secondMilestone.getName(), LocalDate.now());
        String currentMilestoneName = enrollment.getCurrentMilestoneName();

        assertEquals(secondMilestone.getName(), currentMilestoneName);
        List<MilestoneFulfillment> fulfillments = enrollment.getFulfillments();
        assertEquals(1, fulfillments.size());
    }

    @Test
    public void shouldGetReferenceDateAsTheLastFulfilledDateWhenNoMilestoneFulfilled() {
        assertEquals(referenceDate, enrollment.getLastFulfilledDate());
    }

    @Test
    public void shouldGetLastFulfilledDate() {
        LocalDate dateFulfilled = weeksAgo(1);
        enrollment.fulfillMilestone(secondMilestone.getName(), dateFulfilled);

        assertEquals(dateFulfilled, enrollment.getLastFulfilledDate());
    }
}
