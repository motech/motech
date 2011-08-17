package org.motechproject.scheduletracking.api.contract;

import org.motechproject.model.Time;

public class EnrollmentRequest
{
    private String externalId;
    private String scheduleName;
    private String enrolledInMilestone;
    private int enrolledAt;
    private Time preferredAlertTime;

    public String scheduleName() {
        return scheduleName;
    }

    public String externalId() {
        return externalId;
    }

    public String enrolledInMilestone() {
        return enrolledInMilestone;
    }

    public int enrolledAt() {
        return enrolledAt;
    }

    public Time preferredAlertTime() {
        return preferredAlertTime;
    }
}
