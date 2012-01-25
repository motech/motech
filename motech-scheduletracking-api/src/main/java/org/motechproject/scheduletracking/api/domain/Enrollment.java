package org.motechproject.scheduletracking.api.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechBaseDataObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@TypeDiscriminator("doc.type === 'Enrollment'")
public class Enrollment extends MotechBaseDataObject {
	private static final long serialVersionUID = 5097894298798275204L;

	private String externalId;
	private LocalDate enrollmentDate;
	private List<MilestoneFulfillment> fulfillments = new LinkedList<MilestoneFulfillment>();
	private Schedule schedule;
	private LocalDate referenceDate;
	private Milestone currentMilestone;

	// For ektorp
	private Enrollment() {
	}

	public Enrollment(String externalId, Schedule schedule, LocalDate enrollmentDate, LocalDate referenceDate) {
		this.externalId = externalId;
		this.schedule = schedule;
		this.enrollmentDate = enrollmentDate;
		this.referenceDate = referenceDate;
		currentMilestone = schedule.getFirstMilestone();
	}

    public Enrollment(String externalId, Schedule schedule, LocalDate enrollmentDate, LocalDate referenceDate, String startingMilestoneName) throws MilestoneNotPartOfScheduleException {
        this.externalId = externalId;
        this.schedule = schedule;
        this.enrollmentDate = enrollmentDate;
        this.referenceDate = referenceDate;
        Milestone startingMilestone = getMilestoneByName(startingMilestoneName);
        if (startingMilestone != null)
            currentMilestone = startingMilestone;
        else
            throw new MilestoneNotPartOfScheduleException();
    }

    private Milestone getMilestoneByName(String startingMilestoneName) {
        for (Milestone milestone = schedule.getFirstMilestone(); milestone != null; milestone = milestone.getNextMilestone())
            if (milestone.getName().equals(startingMilestoneName))
                return milestone;
        return null;
    }

    @JsonIgnore
	public List<Alert> getAlerts() {
		List<Alert> alerts = new ArrayList<Alert>();
		LocalDate dateFulfilled = referenceDate;

		if (!fulfillments.isEmpty())
			dateFulfilled = fulfillments.get(fulfillments.size() -1).getDateFulfilled();

		WindowName windowName = currentMilestone.getApplicableWindow(dateFulfilled);
		alerts.add(new Alert(windowName, currentMilestone));

		return alerts;
	}

	public void fulfillMilestone() {
		fulfillments.add(new MilestoneFulfillment(currentMilestone, LocalDate.now()));
		currentMilestone = currentMilestone.getNextMilestone();
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public String getExternalId() {
		return externalId;
	}

    // why public?
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

	// For ektorp
	public LocalDate getReferenceDate() {
		return referenceDate;
	}

	// For ektorp
	private void setReferenceDate(LocalDate referenceDate) {
		this.referenceDate = referenceDate;
	}
}
