package org.motechproject.server.pillreminder.api.contract;

import java.util.List;

/**
 * \ingroup pillreminder
 * Daily Pill Regimen Request represents medicine prescription details and reminder configuration.
 * @see DosageRequest
 */
public class DailyPillRegimenRequest {
    private int pillWindowInHours;
    private String externalId;
    private List<DosageRequest> dosageRequests;
    protected int reminderRepeatIntervalInMinutes;
    protected int bufferOverDosageTimeInMinutes;

    /** Creates Daily Pill prescription for given subscriber id and reminder configuration
     *
     * @param externalId unique id for subscriber
     * @param pillWindowInHours duration in hours for reminder retry
     * @param reminderRepeatIntervalInMinutes wait time before 2 reminder retries
     * @param bufferTimeForPatientToTakePill Additional wait time before sending reminder. (Time to take pill typically 5 min)
     *                                       when set to 5, If pill time is 9:00 am then reminder will be set out at 9:05 am.
     * @param dosageRequests Dosage detail with dose time and medicine list. {@link DosageRequest}
     */
    public DailyPillRegimenRequest(String externalId, int pillWindowInHours, int reminderRepeatIntervalInMinutes, int bufferTimeForPatientToTakePill, List<DosageRequest> dosageRequests) {
        this.externalId = externalId;
        this.dosageRequests = dosageRequests;
        this.reminderRepeatIntervalInMinutes = reminderRepeatIntervalInMinutes;
        this.pillWindowInHours = pillWindowInHours;
        this.bufferOverDosageTimeInMinutes = bufferTimeForPatientToTakePill;
    }

    /** Valid time range to retry the reminder in-case of subscriber unreachable*/
    public int getPillWindowInHours() {
        return pillWindowInHours;
    }

    /** Unique Id representing subscriber */
    public String getExternalId() {
        return externalId;
    }

    /**
     * All dosage requests scheduled for pill reminder.
     * @return
     */
    public List<DosageRequest> getDosageRequests() {
        return dosageRequests;
    }

    /**
     * Repeat interval for dosage reminder in-case not able reach subscriber.
     * @return number of minutes to wait before next retry for reminder.
     */
    public int getReminderRepeatIntervalInMinutes() {
        return reminderRepeatIntervalInMinutes;
    }

    public int getBufferOverDosageTimeInMinutes() {
        return bufferOverDosageTimeInMinutes;
    }

    public void setBufferOverDosageTimeInMinutes(int bufferOverDosageTimeInMinutes) {
        this.bufferOverDosageTimeInMinutes = bufferOverDosageTimeInMinutes;
    }
}