package org.motechproject.server.pillreminder.service;

import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;

public interface PillReminderService {
    void createNew(PillRegimenRequest newScheduleRequest);
    void renew(PillRegimenRequest newScheduleRequest);
    void dosageStatusKnown(String pillRegimenId, String dosageId, LocalDate lastCapturedDate);
    PillRegimenResponse getPillRegimen(String externalId);
}
