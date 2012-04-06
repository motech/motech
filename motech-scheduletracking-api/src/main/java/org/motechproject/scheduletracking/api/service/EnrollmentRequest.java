package org.motechproject.scheduletracking.api.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.util.DateUtil.newDateTime;

/**
 * This is the request document to create an enrollment
 */
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

    /**
     * This is the constructor which takes in required parameters to create an enrollment
     * @param externalId
     * @param scheduleName
     * @param preferredAlertTime
     * @param referenceDate - This is the start date of the schedule based on which all the window duration calculations are made.
     *                      In case of enrollment into milestones other than first milestone, this date is not used.
     *                      Default value is today
     * @param referenceTime - This is the start time of the schedule based on which all the window duration calculations are made.
     *                      In case of enrollment into milestones other than first milestone, this time is not used.
     *                      Default value is midnight
     * @param enrollmentDate - The date of enrollment. In case of enrollment into milestones other than first milestone, this becomes the start date of that milestone.
     *                       Default value is today
     * @param enrollmentTime - The time of enrollment. In case of enrollment into milestones other than first milestone, this becomes the start time of that milestone.
     *                       Default value is midnight
     * @param startingMilestoneName - Name of the milestone to enroll against
     *                              Default value is first milestone name from the schedule
     * @param metadata - This is list of string key value pairs associated with an enrollment, which can be used to store some additional information about the enrollment.
     *                 Default value is empty list
     *
     */
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

    /**
     * This returns the External Id of an Enrollment
     * @return String
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * This returns the Schedule Name of an Enrollment
     * @return String
     */
    public String getScheduleName() {
        return scheduleName;
    }

    /**
     * This returns the starting milestone name of an Enrollment
     * @return String
     */
    public String getStartingMilestoneName() {
        return startingMilestoneName;
    }

    /**
     * This returns the preferred alert time of an Enrollment
     * @return String
     */
    public Time getPreferredAlertTime() {
        return preferredAlertTime;
    }

    /**
     * This returns the reference date of an Enrollment
     * @return String
     */
    public LocalDate getReferenceDate() {
		return referenceDate;
	}

    /**
     * This returns the reference time of an Enrollment
     * @return String
     */
    public Time getReferenceTime() {
		return referenceTime != null ? referenceTime : new Time(0,0);
	}

    /**
     * This returns whether the starting milestone has been specified for the Enrollment
     * @return true or false
     */
    public boolean isStartingMilestoneSpecified() {
        return startingMilestoneName != null && !startingMilestoneName.isEmpty();
    }

    /**
     * This returns the enrollment date and time of the enrollment
     * @return DateTime
     */
    public DateTime getEnrollmentDateTime() {
        LocalDate enrollmentDate = this.enrollmentDate != null ? this.enrollmentDate : DateUtil.today();
        Time enrollmentTime = this.enrollmentTime != null ? this.enrollmentTime : new Time(0, 0);
        return newDateTime(enrollmentDate, enrollmentTime);
    }

    /**
     * This returns the reference date and time of the enrollment
     * @return DateTime
     */
    public DateTime getReferenceDateTime() {
        LocalDate referenceDate = this.referenceDate != null ? this.referenceDate : DateUtil.today();
        Time referenceTime = this.referenceTime != null ? this.referenceTime : new Time(0, 0);
        return newDateTime(referenceDate, referenceTime);
    }

    /**
     * This returns the Metadata key value map of an Enrollment
     * @return String
     */
    public Map<String, String> getMetadata() {
        return metadata;
    }

    /**
     * This sets the Metadata key value map of an Enrollment
     */
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
