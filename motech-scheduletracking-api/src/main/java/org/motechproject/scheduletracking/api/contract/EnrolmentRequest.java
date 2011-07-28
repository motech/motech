package org.motechproject.scheduletracking.api.contract;

public class EnrolmentRequest {
    private String externalId;
    private String scheduleName;
    private String enroledInMilestone;
    private int enroledAt;

    public String getScheduleName() {
        return scheduleName;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getEnroledInMilestone() {
        return enroledInMilestone;
    }

    public int getEnroledAt() {
        return enroledAt;
    }
}
