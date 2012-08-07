package org.motechproject.scheduletracking.api.domain;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.weeksAgo;
import static org.motechproject.scheduletracking.api.utility.PeriodUtil.weeks;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.now;

public class EnrollmentTest extends BaseUnitTest {
    @Test
    public void shouldStartWithFirstMilestoneByDefault() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment().setExternalId("ID-074285").setSchedule(schedule).setCurrentMilestoneName("First Shot").setStartOfSchedule(weeksAgo(5)).setEnrolledOn(weeksAgo(3)).setPreferredAlertTime(null).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);

        assertEquals(firstMilestone.getName(), enrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldStartWithSecondMilestone() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment lateEnrollment = new Enrollment().setExternalId("my_entity_1").setSchedule(schedule).setCurrentMilestoneName("Second Shot").setStartOfSchedule(weeksAgo(3)).setEnrolledOn(weeksAgo(3)).setPreferredAlertTime(null).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);

        assertEquals(secondMilestone.getName(), lateEnrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldReNullWhenNoMilestoneIsFulfilled() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment().setExternalId("ID-074285").setSchedule(schedule).setCurrentMilestoneName("First Shot").setStartOfSchedule(weeksAgo(5)).setEnrolledOn(weeksAgo(3)).setPreferredAlertTime(null).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);

        assertEquals(null, enrollment.getLastFulfilledDate());
    }

    @Test
    public void shouldReturnTheDateWhenAMilestoneWasLastFulfilled() {
        mockCurrentDate(new DateTime(2012, 2, 20, 8, 15, 0, 0));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment().setExternalId("ID-074285").setSchedule(schedule).setCurrentMilestoneName("First Shot").setStartOfSchedule(weeksAgo(5)).setEnrolledOn(weeksAgo(3)).setPreferredAlertTime(null).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollment.getFulfillments().add(new MilestoneFulfillment("First Shot", weeksAgo(0)));

        assertEquals(weeksAgo(0), enrollment.getLastFulfilledDate());
    }

    @Test
    public void newEnrollmentShouldBeActive() {
        Schedule schedule = new Schedule("some_schedule");
        Enrollment enrollment = new Enrollment().setExternalId("ID-074285").setSchedule(schedule).setCurrentMilestoneName("First Shot").setStartOfSchedule(weeksAgo(5)).setEnrolledOn(weeksAgo(3)).setPreferredAlertTime(null).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        assertTrue(enrollment.isActive());
    }

    @Test
    public void shouldCopyFromTheGivenEnrollment() {
        Schedule schedule = new Schedule("some_schedule");
        Enrollment newEnrollment = new Enrollment().setExternalId("externalId").setSchedule(schedule).setCurrentMilestoneName("newCurrentMilestoneName").setStartOfSchedule(weeksAgo(2)).setEnrolledOn(now()).setPreferredAlertTime(new Time(8, 10)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        Enrollment originalEnrollment = new Enrollment().setExternalId("externalId").setSchedule(schedule).setCurrentMilestoneName("currentMilestoneName").setStartOfSchedule(weeksAgo(3)).setEnrolledOn(weeksAgo(2)).setPreferredAlertTime(new Time(2, 5)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);

        Enrollment enrollment = originalEnrollment.copyFrom(newEnrollment);

        assertEquals(newEnrollment.getExternalId(), enrollment.getExternalId());
        assertEquals(newEnrollment.getScheduleName(), enrollment.getScheduleName());
        assertEquals(newEnrollment.getCurrentMilestoneName(), enrollment.getCurrentMilestoneName());
        assertEquals(newEnrollment.getStartOfSchedule(), enrollment.getStartOfSchedule());
        assertEquals(newEnrollment.getEnrolledOn(), enrollment.getEnrolledOn());
        assertEquals(newEnrollment.getPreferredAlertTime(), enrollment.getPreferredAlertTime());
    }

    @Test
    public void shouldReturnReferenceDateWhenCurrentMilestoneIsTheFirstMilestone() {
        String firstMilestoneName = "first milestone";
        DateTime referenceDateTime = weeksAgo(5);
        Enrollment enrollment = new Enrollment().setExternalId("ID-074285").setSchedule(getMockedSchedule(firstMilestoneName, false)).setCurrentMilestoneName(firstMilestoneName).setStartOfSchedule(referenceDateTime).setEnrolledOn(weeksAgo(3)).setPreferredAlertTime(new Time(8, 20)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);

        assertEquals(referenceDateTime, enrollment.getCurrentMilestoneStartDate());
    }


    @Test
    public void shouldReturnEnrollmentDateWhenEnrolledIntoSecondMilestoneAndNoMilestonesFulfilled() {
        String firstMilestoneName = "First Shot";
        String secondMilestoneName = "Second Shot";
        DateTime enrollmentDateTime = weeksAgo(3);
        Enrollment enrollment = new Enrollment().setExternalId("ID-074285").setSchedule(getMockedSchedule(firstMilestoneName, false)).setCurrentMilestoneName(secondMilestoneName).setStartOfSchedule(weeksAgo(5)).setEnrolledOn(enrollmentDateTime).setPreferredAlertTime(null).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);

        assertEquals(enrollmentDateTime, enrollment.getCurrentMilestoneStartDate());
    }

    @Test
    public void shouldReturnReferenceDateAsTheMilestoneStartDateOfTheAnyMilestoneWhenTheScheduleIsBasedOnAbsoluteWindows() {
        String firstMilestoneName = "First Milestone";
        String secondMilestoneName = "Second Milestone";
        DateTime referenceDate = weeksAgo(5);

        Enrollment enrollmentIntoFirstMilestone = new Enrollment().setExternalId("ID-074285").setSchedule(getMockedSchedule(firstMilestoneName, true)).setCurrentMilestoneName(firstMilestoneName).setStartOfSchedule(referenceDate).setEnrolledOn(weeksAgo(3)).setPreferredAlertTime(null).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        Enrollment enrollmentIntoSecondMilestone = new Enrollment().setExternalId("ID-074285").setSchedule(getMockedSchedule(firstMilestoneName, true)).setCurrentMilestoneName(secondMilestoneName).setStartOfSchedule(referenceDate).setEnrolledOn(weeksAgo(3)).setPreferredAlertTime(null).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);

        assertEquals(referenceDate, enrollmentIntoFirstMilestone.getCurrentMilestoneStartDate());
        assertEquals(referenceDate, enrollmentIntoSecondMilestone.getCurrentMilestoneStartDate());
    }

    @Test
    public void shouldReturnMilestoneStartForAbsoluteScedule() {
        Schedule schedule = new Schedule("test scedule");
        Milestone milestone1 = new Milestone("milestone 1", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone milestone2 = new Milestone("milestone 2", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone milestone3 = new Milestone("milestone 3", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(milestone1, milestone2, milestone3);
        schedule.isBasedOnAbsoluteWindows(true);

        DateTime now = now();
        Enrollment enrollment = new Enrollment().setExternalId("ID-074285").setSchedule(schedule).setCurrentMilestoneName("milestone 3").setStartOfSchedule(now).setEnrolledOn(now).setPreferredAlertTime(new Time(0, 0)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);

        assertEquals(now.plusWeeks(8), enrollment.getCurrentMilestoneStartDate());
    }

    @Test
    public void shouldFulfillCurrentMilestone() {
        Schedule schedule = new Schedule("some_schedule");
        Enrollment enrollment = new Enrollment().setExternalId("externalId").setSchedule(schedule).setCurrentMilestoneName("currentMilestoneName").setStartOfSchedule(weeksAgo(1)).setEnrolledOn(weeksAgo(1)).setPreferredAlertTime(new Time(8, 10)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);

        assertEquals(0, enrollment.getFulfillments().size());
        enrollment.fulfillCurrentMilestone(DateUtil.newDateTime(2011, 6, 5, 0, 0, 0));
        assertEquals(1, enrollment.getFulfillments().size());

        MilestoneFulfillment milestoneFulfillment = enrollment.getFulfillments().get(0);
        assertEquals(newDateTime(2011, 6, 5, 0, 0, 0), milestoneFulfillment.getFulfillmentDateTime());
        assertEquals("currentMilestoneName", milestoneFulfillment.getMilestoneName());
    }

    private Schedule getMockedSchedule(String firstMilestoneName, boolean isBasedOnAbsoluteWindows) {
        Schedule schedule = mock(Schedule.class);
        Milestone milestone = mock(Milestone.class);
        when(milestone.getName()).thenReturn(firstMilestoneName);
        when(schedule.getFirstMilestone()).thenReturn(milestone);
        when(schedule.isBasedOnAbsoluteWindows()).thenReturn(isBasedOnAbsoluteWindows);
        return schedule;
    }

    @Test
    public void shouldReturnTheStartOfAGivenWindowForTheCurrentMilestone() {
        Milestone firstMilestone = new Milestone("first_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone);

        DateTime referenceDate = newDateTime(2012, 12, 4, 8, 30, 0);
        Enrollment enrollment = new Enrollment().setExternalId("ID-074285").setSchedule(schedule).setCurrentMilestoneName("first_milestone").setStartOfSchedule(referenceDate).setEnrolledOn(referenceDate).setPreferredAlertTime(null).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);

        Assert.assertEquals(referenceDate, enrollment.getStartOfWindowForCurrentMilestone(WindowName.earliest));
        Assert.assertEquals(referenceDate.plusWeeks(1), enrollment.getStartOfWindowForCurrentMilestone(WindowName.due));
        Assert.assertEquals(referenceDate.plusWeeks(2), enrollment.getStartOfWindowForCurrentMilestone(WindowName.late));
        Assert.assertEquals(referenceDate.plusWeeks(3), enrollment.getStartOfWindowForCurrentMilestone(WindowName.max));
    }}
