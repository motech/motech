package org.motechproject.scheduletracking.api.contract;

import org.motechproject.model.Time;

public class EnrollmentRequest {
    private String externalId;
    private String scheduleName;
    private String enrolledInMilestone;
	private Time preferredAlertTime;

    public EnrollmentRequest(String externalId, String scheduleName, String enrolledInMilestone, Time preferredAlertTime) {
        this.externalId = externalId;
        this.scheduleName = scheduleName;
        this.enrolledInMilestone = enrolledInMilestone;
        this.preferredAlertTime = preferredAlertTime;
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
}
