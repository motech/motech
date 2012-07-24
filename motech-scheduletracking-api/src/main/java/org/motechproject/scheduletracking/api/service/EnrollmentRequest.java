package org.motechproject.scheduletracking.api.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.today;

/**
 * \ingroup sts
 *
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

    public EnrollmentRequest() {
        this.referenceDate = today();
        this.referenceTime = new Time(0, 0);
        this.enrollmentDate = today();
        this.enrollmentTime = new Time(0, 0);
        this.metadata = new HashMap<>();
    }

    /**
     * This returns the External Id of the Enrollment
     * @return String
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * This returns the Schedule Name of the Enrollment
     * @return String
     */
    public String getScheduleName() {
        return scheduleName;
    }

    /**
     * This returns the starting milestone name of the Enrollment
     * @return String
     */
    public String getStartingMilestoneName() {
        return startingMilestoneName;
    }

    /**
     * This returns the preferred alert time of the Enrollment
     * @return String
     */
    public Time getPreferredAlertTime() {
        return preferredAlertTime;
    }

    /**
     * This returns the reference date of the Enrollment
     * @return String
     */
    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    /**
     * This returns the reference time of the Enrollment
     * @return String
     */
    public Time getReferenceTime() {
        return referenceTime != null ? referenceTime : new Time(0, 0);
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
        return newDateTime(enrollmentDate, enrollmentTime);
    }

    /**
     * This returns the reference date and time of the enrollment
     * @return DateTime
     */
    public DateTime getReferenceDateTime() {
        return newDateTime(referenceDate, referenceTime);
    }

    /**
     * This returns the Metadata key value map of the Enrollment
     * @return String
     */
    public Map<String, String> getMetadata() {
        return metadata;
    }

    /**
     * This sets the Metadata key value map of the Enrollment. This is list of string key value pairs associated with an enrollment, which can be used to store some additional information about the enrollment.
     * Default value is empty list
     */
    public EnrollmentRequest setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * This sets the external id of the Enrollment
     */
    public EnrollmentRequest setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    /**
     * This sets the schedule of the Enrollment
     */
    public EnrollmentRequest setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
        return this;
    }

    /**
     * This sets the preferred alert time of the Enrollment
     */
    public EnrollmentRequest setPreferredAlertTime(Time preferredAlertTime) {
        this.preferredAlertTime = preferredAlertTime;
        return this;
    }

    /**
     * This sets the reference date of the Enrollment. This is the start date of the schedule based on which all the window duration calculations are made.
     * In case of enrollment into milestones other than first milestone, this date is not used.
     * Default value is today
     */
    public EnrollmentRequest setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate == null ? today() : referenceDate;
        return this;
    }

    /**
     * This sets the reference time of the Enrollment. This is the start time of the schedule based on which all the window duration calculations are made.
     * In case of enrollment into milestones other than first milestone, this time is not used.
     * Default value is midnight
     */
    public EnrollmentRequest setReferenceTime(Time referenceTime) {
        this.referenceTime = referenceTime == null ? new Time(0, 0) : referenceTime;
        return this;
    }

    /**
     * This sets the enrollment date of the Enrollment. The date of enrollment. In case of enrollment into milestones other than first milestone, this becomes the start date of that milestone.
     * Default value is today
     */
    public EnrollmentRequest setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate == null ? today() : enrollmentDate;
        return this;
    }

    /**
     * This sets the enrollment time of the Enrollment. The time of enrollment. In case of enrollment into milestones other than first milestone, this becomes the start time of that milestone.
     * Default value is midnight
     */
    public EnrollmentRequest setEnrollmentTime(Time enrollmentTime) {
        this.enrollmentTime = enrollmentTime == null ? new Time(0, 0) : enrollmentTime;
        return this;
    }

    /**
     * This sets the starting milestone of the Enrollment. Name of the milestone to enroll against
     * Default value is first milestone name from the schedule
     */
    public EnrollmentRequest setStartingMilestoneName(String startingMilestoneName) {
        this.startingMilestoneName = startingMilestoneName;
        return this;
    }
}
