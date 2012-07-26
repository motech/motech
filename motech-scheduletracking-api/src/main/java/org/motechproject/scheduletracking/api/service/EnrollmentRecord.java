package org.motechproject.scheduletracking.api.service;

import org.joda.time.DateTime;
import org.motechproject.model.Time;
/**
 * \ingroup sts
 *
 * This is the record which will be returned when schedule tracking service is queried for enrollments
 * It holds the details of an enrollment
 *
 */
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

    public EnrollmentRecord() {
    }

    /**
     * This returns the External Id of an EnrollmentRecord
     * @return String
     */
    public String getExternalId() {
        return externalId;
    }

    
    /**
     * This returns the Schedule Name of an EnrollmentRecord
     * @return String
     */
    public String getScheduleName() {
        return scheduleName;
    }

    /**
     * This returns the preferred alert time of an EnrollmentRecord
     * @return Time
     */
    public Time getPreferredAlertTime() {
        return preferredAlertTime;
    }

    /**
     * This returns the reference datetime of an EnrollmentRecord
     * @return DateTime
     */
    public DateTime getReferenceDateTime() {
        return referenceDateTime;
    }

    /**
     * This returns the enrollment datetime of an EnrollmentRecord
     * @return DateTime
     */
    public DateTime getEnrollmentDateTime() {
        return enrollmentDateTime;
    }

    /**
     * This returns the earliest window start datetime of current milestone of an EnrollmentRecord
     * @return DateTime
     */
    public DateTime getStartOfEarliestWindow() {
        return earliestStart;
    }

    /**
     * This returns the due window start datetime of current milestone of an EnrollmentRecord
     * @return DateTime
     */
    public DateTime getStartOfDueWindow() {
        return dueStart;
    }

    /**
     * This returns the late window start datetime of current milestone of an EnrollmentRecord
     * @return DateTime
     */
    public DateTime getStartOfLateWindow() {
        return lateStart;
    }

    /**
     * This returns the max window start datetime of current milestone of an EnrollmentRecord
     * @return DateTime
     */
    public DateTime getStartOfMaxWindow() {
        return maxStart;
    }

    /**
     * This returns the current milestone name of an EnrollmentRecord
     * @return String
     */
    public String getCurrentMilestoneName() {
        return currentMilestoneName;
    }

    public EnrollmentRecord setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public EnrollmentRecord setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
        return this;
    }

    public EnrollmentRecord setPreferredAlertTime(Time preferredAlertTime) {
        this.preferredAlertTime = preferredAlertTime;
        return this;
    }

    public EnrollmentRecord setReferenceDateTime(DateTime referenceDateTime) {
        this.referenceDateTime = referenceDateTime;
        return this;
    }

    public EnrollmentRecord setEnrollmentDateTime(DateTime enrollmentDateTime) {
        this.enrollmentDateTime = enrollmentDateTime;
        return this;
    }

    public EnrollmentRecord setEarliestStart(DateTime earliestStart) {
        this.earliestStart = earliestStart;
        return this;
    }

    public EnrollmentRecord setDueStart(DateTime dueStart) {
        this.dueStart = dueStart;
        return this;
    }

    public EnrollmentRecord setLateStart(DateTime lateStart) {
        this.lateStart = lateStart;
        return this;
    }

    public EnrollmentRecord setMaxStart(DateTime maxStart) {
        this.maxStart = maxStart;
        return this;
    }

    public EnrollmentRecord setCurrentMilestoneName(String currentMilestoneName) {
        this.currentMilestoneName = currentMilestoneName;
        return this;
    }
}
