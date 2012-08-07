package org.motechproject.server.pillreminder.api.service;
/**
 * \defgroup pillreminder Pill Reminder
 */
import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.api.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.api.contract.PillRegimenResponse;

/**
 * \ingroup pillreminder
 * Pill reminder service supports creating/querying/deleting pill schedule as per prescription.
 * @see org.motechproject.server.pillreminder.api.contract.DailyPillRegimenRequest
 */

public interface PillReminderService {
    /**
     * Subscribe to pill reminder
     * @param dailyPillRegimenRequest
     * @see org.motechproject.server.pillreminder.api.contract.DailyPillRegimenRequest
     */
    void createNew(DailyPillRegimenRequest dailyPillRegimenRequest);

    /**
     * Update the pill reminder subscription
     * @param newDailyScheduleRequest
     * @see org.motechproject.server.pillreminder.api.contract.DailyPillRegimenRequest
     */
    void renew(DailyPillRegimenRequest newDailyScheduleRequest);

    /**
     * Update the dosage take status
     * @param pillRegimenId subscription id
     * @param dosageId  Dosage id
     * @param lastCapturedDate Dosage confirmation captured date.
     */
    void dosageStatusKnown(String pillRegimenId, String dosageId, LocalDate lastCapturedDate);

    /**
     * Get pill regimen for given subscriber (externalId)
     * @param externalId
     * @return Dosage details along with reminder config
     * @see org.motechproject.server.pillreminder.api.contract.PillRegimenResponse
     */
    PillRegimenResponse getPillRegimen(String externalId);

    /**
     * Unsubscribe from pill reminder service.
     * @param externalID Unique subscriber id.
     */
    void remove(String externalID);
}
