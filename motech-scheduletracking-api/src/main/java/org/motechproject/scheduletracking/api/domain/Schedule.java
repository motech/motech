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
		this.name = name;
		this.totalDuration = totalDuration;
		this.firstMilestone = firstMilestone;
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
		Milestone milestone = firstMilestone;
		while (milestone != null && !milestone.hasName(milestoneName))
			milestone = milestone.getNextMilestone();
		return milestone;
	}

	public LocalDate getEndDate(LocalDate startDate) {
		return startDate.plusDays(totalDuration.inDays());
	}

	public String getNextMilestone(String milestoneName) {
		Milestone milestone = getMilestone(milestoneName);
		Milestone next = milestone.getNextMilestone();

		return next == null ? null : next.getName();
	}
}
