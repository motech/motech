package org.motechproject.scheduletracking.api.domain;

import org.junit.Test;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MilestoneWindowTest extends BaseScheduleTrackingTest {
	@Test
	public void shouldCheckIfGivenDateFallsWithinTheWindow() {
		MilestoneWindow milestoneWindow = new MilestoneWindow(new WallTime(0, WallTimeUnit.Week), new WallTime(13, WallTimeUnit.Week));
		assertTrue("Date falls outside the window.", milestoneWindow.isApplicableTo(weeksAgo(0)));
		assertTrue("Date falls outside the window.", milestoneWindow.isApplicableTo(weeksAgo(10)));
		assertFalse("Date falls within the window.", milestoneWindow.isApplicableTo(weeksAgo(13)));
	}

	@Test
	public void shouldCheckIfGivenDateFallsWithinTheWindowWhenBeginningAndEndOfWindowAreTheSame() {
		MilestoneWindow milestoneWindow = new MilestoneWindow(new WallTime(16, WallTimeUnit.Week), null);
		assertFalse("Date falls within the window.", milestoneWindow.isApplicableTo(weeksAgo(15)));
		assertTrue("Date falls outside the window.", milestoneWindow.isApplicableTo(weeksAgo(16)));
		assertFalse("Date falls within the window.", milestoneWindow.isApplicableTo(weeksAgo(17)));
	}
}
