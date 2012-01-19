package org.motechproject.scheduletracking.api.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.LocalDate;
import org.motechproject.valueobjects.WallTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Schedule implements Serializable {
	private static final long serialVersionUID = 2783395208102730624L;

	@JsonProperty
	private String name;
	@JsonProperty
	private WallTime totalDuration;
	@JsonProperty
	private Milestone firstMilestone;

	// For ektorp
	private Schedule() {
	}

	public Schedule(String name, WallTime totalDuration, Milestone firstMilestone) {
		this.name = name;
		this.totalDuration = totalDuration;
		this.firstMilestone = firstMilestone;
	}

	public List<Alert> getAlertsFor(LocalDate enrolledDate, Milestone milestone) {
		List<Alert> alerts = new ArrayList<Alert>();

		WindowName windowName = milestone.getApplicableWindow(enrolledDate);
		if (WindowName.Due.compareTo(windowName) <= 0) {
			alerts.add(new Alert(windowName, milestone));
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

	public Milestone getNextMilestone(Milestone milestone) {
		return milestone.getNextMilestone();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Schedule schedule = (Schedule) o;

		if (name != null ? !name.equals(schedule.name) : schedule.name != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}
}
