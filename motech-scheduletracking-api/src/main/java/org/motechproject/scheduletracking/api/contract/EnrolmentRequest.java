package org.motechproject.scheduletracking.api.contract;

import org.motechproject.model.Time;

public class EnrolmentRequest {
    private String externalId;
    private String scheduleName;
    private String enroledInMilestone;
    private int enroledAt;
    private Time preferredAlertTime;

    public String scheduleName() {
        return scheduleName;
    }

    public String externalId() {
        return externalId;
    }

    public String enroledInMilestone() {
        return enroledInMilestone;
    }

    public int enroledAt() {
        return enroledAt;
    }

    public Time preferredAlertTime() {
        return preferredAlertTime;
    }
}
