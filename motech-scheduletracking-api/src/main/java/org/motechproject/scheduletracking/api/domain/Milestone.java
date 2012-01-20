package org.motechproject.scheduletracking.api.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.LocalDate;
import org.motechproject.valueobjects.WallTime;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class Milestone implements Serializable {
	private static final long serialVersionUID = 5006000470852287833L;

	@JsonProperty
	private String name;
	@JsonProperty
	private Map<WindowName, MilestoneWindow> windows = new EnumMap<WindowName, MilestoneWindow>(WindowName.class);
	@JsonProperty
	private Map<String, String> data;
	@JsonProperty
	private Milestone nextMilestone;

	// For ektorp
	private Milestone() {
	}

	public Milestone(String name, Milestone nextMilestone, WallTime earliest, WallTime due, WallTime late, WallTime max) {
		this.nextMilestone = nextMilestone;
		this.name = name;

		windows.put(WindowName.Waiting, new MilestoneWindow(new WallTime(0, earliest.getUnit()), earliest));
		windows.put(WindowName.Upcoming, new MilestoneWindow(earliest, due));
		windows.put(WindowName.Due, new MilestoneWindow(due, late));
		windows.put(WindowName.Late, new MilestoneWindow(late, max));
	}

	public Milestone(String name, WallTime earliest, WallTime due, WallTime late, WallTime max) {
		this(name, null, earliest, due, late, max);
	}

	public MilestoneWindow getMilestoneWindow(WindowName windowName) {
		return windows.get(windowName);
	}

	public WindowName getApplicableWindow(LocalDate enrollmentDate) {
		Set<Map.Entry<WindowName, MilestoneWindow>> entries = windows.entrySet();
		for (Map.Entry<WindowName, MilestoneWindow> entry : entries) {
			MilestoneWindow milestoneWindow = entry.getValue();
			if (milestoneWindow.isApplicableTo(enrollmentDate))
				return entry.getKey();
		}
		return WindowName.Past;
	}

	public String getName() {
		return name;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public Map<String, String> getData() {
		return data;
	}

	public Milestone getNextMilestone() {
		return nextMilestone;
	}

	public boolean hasName(String milestoneName) {
		return name.equals(milestoneName);
	}
}
