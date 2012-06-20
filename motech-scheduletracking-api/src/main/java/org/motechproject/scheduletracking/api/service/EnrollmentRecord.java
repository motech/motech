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

    /**
     * This is the constructor which is used to create an EnrollmentRecord
     * @param externalId
     * @param scheduleName
     * @param currentMilestoneName
     * @param preferredAlertTime
     * @param referenceDateTime
     * @param enrollmentDateTime
     * @param earliestStart
     * @param dueStart
     * @param lateStart
     * @param maxStart
     */
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
}
