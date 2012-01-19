package org.motechproject.scheduletracking.api.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechBaseDataObject;

import java.util.LinkedList;
import java.util.List;

@TypeDiscriminator("doc.type === 'Enrollment'")
public class Enrollment extends MotechBaseDataObject {
	private static final long serialVersionUID = 5097894298798275204L;

	private String externalId;
	private LocalDate enrollmentDate;
	private List<MilestoneFulfillment> fulfillments = new LinkedList<MilestoneFulfillment>();
	private Schedule schedule;
	private Milestone currentMilestone;

	// For ektorp
	private Enrollment() {
	}

	public Enrollment(String externalId, LocalDate enrollmentDate, Schedule schedule) {
		this.externalId = externalId;
		this.enrollmentDate = enrollmentDate;
		this.schedule = schedule;
		currentMilestone = schedule.getFirstMilestone();
	}

	@JsonIgnore
	public List<Alert> getAlerts() {
		LocalDate dateLastFulfilled = enrollmentDate;

		if (!fulfillments.isEmpty()) {
			MilestoneFulfillment fulfillment = fulfillments.get(fulfillments.size() - 1);
			dateLastFulfilled = fulfillment.getDateFulfilled();
		}

		return schedule.getAlertsFor(dateLastFulfilled, currentMilestone);
	}

	public void fulfillMilestone() {
		fulfillMilestone(LocalDate.now());
	}

	public void fulfillMilestone(LocalDate fulfilledOn) {
		fulfillments.add(new MilestoneFulfillment(currentMilestone, fulfilledOn));
		currentMilestone = schedule.getNextMilestone(currentMilestone);
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public String getExternalId() {
		return externalId;
	}

	public Milestone getCurrentMilestone() {
		return currentMilestone;
	}

	public List<MilestoneFulfillment> getFulfillments() {
		return fulfillments;
	}

	// For ektorp
	private void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	// For ektorp
	private String getType() {
		return type;
	}

	// For ektorop
	private void setType(String type) {
		this.type = type;
	}

	// For ektorp
	private void setCurrentMilestone(Milestone milestone) {
		currentMilestone = milestone;
	}

	// For ektorp
	private void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	// For ektorp
	private LocalDate getEnrollmentDate() {
		return enrollmentDate;
	}

	// For ektorp
	private void setEnrollmentDate(LocalDate enrollmentDate) {
		this.enrollmentDate = enrollmentDate;
	}
}
