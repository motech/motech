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
 * @see DailyPillRegimenRequest
 */

public interface PillReminderService {
    void createNew(DailyPillRegimenRequest dailyPillRegimenRequest);
    void renew(DailyPillRegimenRequest newDailyScheduleRequest);
    void dosageStatusKnown(String pillRegimenId, String dosageId, LocalDate lastCapturedDate);
    PillRegimenResponse getPillRegimen(String externalId);
    void remove(String externalID);
}
