package org.motechproject.scheduletracking.api.service;

import org.joda.time.DateTime;
import org.motechproject.model.Time;

public class EnrollmentRecord {
    private String externalId;
    private String scheduleName;
	private Time preferredAlertTime;
	private DateTime referenceDateTime;
    private DateTime enrollmentDateTime;
    private DateTime earliestStart;
    private DateTime dueStart;
    private DateTime lateStart;
    private DateTime maxStart;
    private String currentMilestoneName;


    public EnrollmentRecord(String externalId, String scheduleName, String currentMilestoneName, Time preferredAlertTime, DateTime referenceDateTime, DateTime enrollmentDateTime, DateTime earliestStart, DateTime dueStart, DateTime lateStart, DateTime maxStart) {
        this.externalId = externalId;
        this.scheduleName = scheduleName;
        this.currentMilestoneName = currentMilestoneName;
        this.preferredAlertTime = preferredAlertTime;
        this.referenceDateTime = referenceDateTime;
        this.enrollmentDateTime = enrollmentDateTime;
        this.earliestStart = earliestStart;
        this.dueStart = dueStart;
        this.lateStart = lateStart;
        this.maxStart = maxStart;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public Time getPreferredAlertTime() {
        return preferredAlertTime;
    }

    public DateTime getReferenceDateTime() {
        return referenceDateTime;
    }

    public DateTime getEnrollmentDateTime() {
        return enrollmentDateTime;
    }

    public DateTime getStartOfEarliestWindow() {
        return earliestStart;
    }

    public DateTime getStartOfDueWindow() {
        return dueStart;
    }

    public DateTime getStartOfLateWindow() {
        return lateStart;
    }

    public DateTime getStartOfMaxWindow() {
        return maxStart;
    }


    public String getCurrentMilestoneName() {
        return currentMilestoneName;
    }
}
