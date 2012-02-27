package org.motechproject.scheduletracking.api.service;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;

public class EnrollmentRequest {
    private String externalId;
    private String scheduleName;
	private Time preferredAlertTime;
	private LocalDate referenceDate;
    private LocalDate enrollmentDate;
    private String startingMilestoneName;

    public EnrollmentRequest(String externalId, String scheduleName, Time preferredAlertTime, LocalDate referenceDate, LocalDate enrollmentDate, String startingMilestoneName) {
        this.externalId = externalId;
        this.scheduleName = scheduleName;
        this.preferredAlertTime = preferredAlertTime;
        this.referenceDate = referenceDate;
        this.enrollmentDate = enrollmentDate;
        this.startingMilestoneName = startingMilestoneName;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public String getStartingMilestoneName() {
        return startingMilestoneName;
    }

    public Time getPreferredAlertTime() {
        return preferredAlertTime;
    }

	public LocalDate getReferenceDate() {
		return referenceDate;
	}

    public boolean enrollIntoMilestone() {
        return startingMilestoneName != null && !startingMilestoneName.isEmpty();
    }

    public LocalDate enrollmentDate() {
        return enrollmentDate != null ? enrollmentDate : DateUtil.today();
    }
}
