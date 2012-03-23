package org.motechproject.scheduletracking.api.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.util.DateUtil.newDateTime;

public class EnrollmentRequest {
    private String externalId;
    private String scheduleName;
	private Time preferredAlertTime;
	private LocalDate referenceDate;
	private Time referenceTime;
    private LocalDate enrollmentDate;
    private Time enrollmentTime;
    private String startingMilestoneName;
    private Map<String, String> metadata;

    public EnrollmentRequest(String externalId, String scheduleName, Time preferredAlertTime, LocalDate referenceDate, Time referenceTime, LocalDate enrollmentDate, Time enrollmentTime, String startingMilestoneName, Map<String, String> metadata) {
        this.externalId = externalId;
        this.scheduleName = scheduleName;
        this.preferredAlertTime = preferredAlertTime;
        this.referenceDate = referenceDate;
        this.referenceTime = referenceTime;
        this.enrollmentDate = enrollmentDate;
        this.enrollmentTime = enrollmentTime;
        this.startingMilestoneName = startingMilestoneName;
        this.metadata = (metadata != null)? metadata : new HashMap<String, String>();
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

	public Time getReferenceTime() {
		return referenceTime != null ? referenceTime : new Time(0,0);
	}

    public boolean enrollIntoMilestone() {
        return startingMilestoneName != null && !startingMilestoneName.isEmpty();
    }

    public DateTime getEnrollmentDateTime() {
        LocalDate enrollmentDate = this.enrollmentDate != null ? this.enrollmentDate : DateUtil.today();
        Time enrollmentTime = this.enrollmentTime != null ? this.enrollmentTime : new Time(0, 0);
        return newDateTime(enrollmentDate, enrollmentTime);
    }

    public DateTime getReferenceDateTime() {
        LocalDate referenceDate = this.referenceDate != null ? this.referenceDate : DateUtil.today();
        Time referenceTime = this.referenceTime != null ? this.referenceTime : new Time(0, 0);
        return newDateTime(referenceDate, referenceTime);
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
