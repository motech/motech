package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.motechproject.valueobjects.WallTime;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
	private String name;
	private WallTime totalDuration;
	private Milestone firstMilestone;

	public Schedule(String name, WallTime totalDuration, Milestone firstMilestone) {
		this.firstMilestone = firstMilestone;
		this.name = name;
		this.totalDuration = totalDuration;
	}

	public List<Alert> alertsFor(LocalDate enrolledDate, String dueMilestoneName) {
		List<Alert> alerts = new ArrayList<Alert>();

		Milestone dueMilestone = getMilestone(dueMilestoneName);

		WindowName windowName = dueMilestone.getApplicableWindow(enrolledDate);
		if (WindowName.Due.compareTo(windowName) <= 0) {
			alerts.add(new Alert(windowName, dueMilestone));
		}

		return alerts;
	}

	public Milestone getFirstMilestone() {
		return firstMilestone;
	}

	public String getName() {
		return name;
	}

	public Milestone getMilestone(String milestoneName) {
		Milestone result = getFirstMilestone();
		while (result != null && !result.getName().equals(milestoneName)) result = result.getNextMilestone();
		return result;
	}

	public LocalDate getEndDate(LocalDate startDate) {
		return startDate.plusDays(totalDuration.inDays());
	}

	public String nextMilestone(String milestoneName) {
		Milestone milestone = getMilestone(milestoneName);
		Milestone next = milestone.getNextMilestone();

		return next == null ? null : next.getName();
	}
}
