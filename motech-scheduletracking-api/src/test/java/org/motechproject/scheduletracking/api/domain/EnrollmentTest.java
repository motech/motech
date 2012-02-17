package org.motechproject.scheduletracking.api.domain;

import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.model.Time;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.wallTimeOf;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.weeksAgo;
import static org.motechproject.util.DateUtil.today;

public class EnrollmentTest {

    @Test
    public void shouldStartWithFirstMilestoneByDefault() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(5), weeksAgo(3), null);

        assertEquals(firstMilestone.getName(), enrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldStartWithSecondMilestone() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment lateEnrollment = new Enrollment("my_entity_1", "Yellow Fever Vaccination", "Second Shot", weeksAgo(3), weeksAgo(3), null);

        assertEquals(secondMilestone.getName(), lateEnrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldReNullWhenNoMilestoneIsFulfilledlled() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(5), weeksAgo(3), null);

        assertEquals(null, enrollment.lastFulfilledDate());
    }

    @Test
    public void shouldReturnTheDateWhenAMilestoneWasLastFulfilled() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(5), weeksAgo(3), null);
        enrollment.getFulfillments().add(new MilestoneFulfillment("First Shot", weeksAgo(0)));

        assertEquals(weeksAgo(0), enrollment.lastFulfilledDate());
    }
    
    @Test
    public void newEnrollmentShouldBeActive() {
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(5), weeksAgo(3), null);
        assertTrue(enrollment.isActive());
    }
    
    @Test
    public void shouldCopyFromTheGivenEnrollment() {
        Enrollment newEnrollment = new Enrollment("externalId", "scheduleName", "newCurrentMilestoneName", weeksAgo(2), today(), new Time(8, 10));
        Enrollment originalEnrollment = new Enrollment("externalId", "scheduleName", "currentMilestoneName", weeksAgo(3), weeksAgo(2), new Time(2, 5));

        Enrollment enrollment = originalEnrollment.copyFrom(newEnrollment);
        
        Assert.assertEquals(newEnrollment.getExternalId(), enrollment.getExternalId());
        Assert.assertEquals(newEnrollment.getScheduleName(), enrollment.getScheduleName());
        Assert.assertEquals(newEnrollment.getCurrentMilestoneName(), enrollment.getCurrentMilestoneName());
        Assert.assertEquals(newEnrollment.getReferenceDate(), enrollment.getReferenceDate());
        Assert.assertEquals(newEnrollment.getEnrollmentDate(), enrollment.getEnrollmentDate());
        Assert.assertEquals(newEnrollment.getPreferredAlertTime(), enrollment.getPreferredAlertTime());
    }
}
