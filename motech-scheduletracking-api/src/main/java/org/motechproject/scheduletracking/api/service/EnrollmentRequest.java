package org.motechproject.scheduletracking.api.service;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;

public class EnrollmentRequest {
    private String externalId;
    private String scheduleName;
	private Time preferredAlertTime;
	private LocalDate referenceDate;

	public EnrollmentRequest(String externalId, String scheduleName, Time preferredAlertTime, LocalDate referenceDate) {
        this.externalId = externalId;
        this.scheduleName = scheduleName;
        this.preferredAlertTime = preferredAlertTime;
		this.referenceDate = referenceDate;
	}

    public String getScheduleName() {
        return scheduleName;
    }

    public String getExternalId() {
        return externalId;
    }

    public Time getPreferredAlertTime() {
        return preferredAlertTime;
    }

	public LocalDate getReferenceDate() {
		return referenceDate;
	}
}
