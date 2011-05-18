package org.motechproject.pillreminder.api.model;

import java.util.ArrayList;
import java.util.List;

public class Medicine {

	private String name;
	private List<Status> statuses = new ArrayList<Status>();

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

	@Override
	public String toString() {
		return "Medicine [name=" + name + ", statuses=" + statuses + "]";
	}
	
}
