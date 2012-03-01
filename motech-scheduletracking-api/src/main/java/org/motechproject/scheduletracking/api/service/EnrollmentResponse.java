package org.motechproject.scheduletracking.api.service;

import org.joda.time.DateTime;
import org.motechproject.model.Time;

public class EnrollmentResponse {
    private String externalId;
    private String scheduleName;
	private Time preferredAlertTime;
	private DateTime referenceDateTime;
    private DateTime enrollmentDateTime;

    public EnrollmentResponse(String externalId, String scheduleName, Time preferredAlertTime, DateTime referenceDateTime, DateTime enrollmentDateTime) {
        this.externalId = externalId;
        this.scheduleName = scheduleName;
        this.preferredAlertTime = preferredAlertTime;
        this.referenceDateTime = referenceDateTime;
        this.enrollmentDateTime = enrollmentDateTime;
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
}
