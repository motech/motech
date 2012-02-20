package org.motechproject.scheduletracking.api.service;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;

public class EnrollmentResponse {
    private String externalId;
    private String scheduleName;
	private Time preferredAlertTime;
	private LocalDate referenceDate;
    private LocalDate enrollmentDate;

    public EnrollmentResponse(String externalId, String scheduleName, Time preferredAlertTime, LocalDate referenceDate, LocalDate enrollmentDate) {
        this.externalId = externalId;
        this.scheduleName = scheduleName;
        this.preferredAlertTime = preferredAlertTime;
        this.referenceDate = referenceDate;
        this.enrollmentDate = enrollmentDate;
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

    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }
}
