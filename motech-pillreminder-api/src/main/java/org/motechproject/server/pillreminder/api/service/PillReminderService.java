package org.motechproject.server.pillreminder.api.service;

import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.api.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.api.contract.PillRegimenResponse;

public interface PillReminderService {
    void createNew(DailyPillRegimenRequest dailyPillRegimenRequest);
    void renew(DailyPillRegimenRequest newDailyScheduleRequest);
    void dosageStatusKnown(String pillRegimenId, String dosageId, LocalDate lastCapturedDate);
    PillRegimenResponse getPillRegimen(String externalId);
    void remove(String externalID);
}
