package org.motechproject.scheduletracking.api.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.LocalDate;

import java.io.Serializable;

public class MilestoneFulfillment implements Serializable {
	private static final long serialVersionUID = 486156182281115607L;

	@JsonProperty
	private Milestone milestone;
	@JsonProperty
	private LocalDate dateFulfilled;

	private MilestoneFulfillment() {
	}

	public MilestoneFulfillment(Milestone milestone, LocalDate dateFulfilled) {
		this.milestone = milestone;
		this.dateFulfilled = dateFulfilled;
	}

	public LocalDate getDateFulfilled() {
		return dateFulfilled;
	}

	public void setDateFulfilled(LocalDate dateFulfilled) {
		this.dateFulfilled = dateFulfilled;
	}

	public Milestone getMilestone() {
		return milestone;
	}

	public void setMilestoneName(Milestone milestone) {
		this.milestone = milestone;
	}
}
