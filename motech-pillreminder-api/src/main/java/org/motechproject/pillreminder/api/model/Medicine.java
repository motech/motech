package org.motechproject.pillreminder.api.model;

import java.util.List;

public class Medicine {

	private String name;
	private List<Status> statuses;

	public List<Status> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<Status> statuses) {
		this.statuses = statuses;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
