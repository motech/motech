package org.motechproject.scheduletracking.api.contract;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;

public class EnrollmentRequest {
    private String externalId;
    private String scheduleName;
    private String enrolledInMilestone;
	private Time preferredAlertTime;
	private LocalDate referenceDate;

	public EnrollmentRequest(String externalId, String scheduleName, String enrolledInMilestone, Time preferredAlertTime, LocalDate referenceDate) {
        this.externalId = externalId;
        this.scheduleName = scheduleName;
        this.enrolledInMilestone = enrolledInMilestone;
        this.preferredAlertTime = preferredAlertTime;
		this.referenceDate = referenceDate;
	}

    public String getScheduleName() {
        return scheduleName;
    }

    public String getExternalId() {
        return externalId;
    }

    public String enrolledInMilestone() {
        return enrolledInMilestone;
    }

    public Time preferredAlertTime() {
        return preferredAlertTime;
    }

	public LocalDate getReferenceDate() {
		return referenceDate;
	}
}
