package org.motechproject.server.pillreminder.service;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;

import java.util.List;

public interface PillReminderService {
    void createNew(PillRegimenRequest newScheduleRequest);
    void renew(PillRegimenRequest newScheduleRequest);
    void stopTodaysReminders(String pillRegimenId, String dosageId);
    PillRegimenResponse getPillRegimen(String externalId);

    List<String> medicinesFor(String pillRegimenId, String dosageId);
    DosageResponse getPreviousDosage(String pillRegimenId, String dosageId);
    DateTime getNextDosageTime(String pillRegimenId, String dosageId);
}
