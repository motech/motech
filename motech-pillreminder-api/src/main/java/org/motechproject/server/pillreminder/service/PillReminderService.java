package org.motechproject.server.pillreminder.service;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;

import java.util.List;

public interface PillReminderService {
    void createNew(PillRegimenRequest newScheduleRequest);
    void renew(PillRegimenRequest newScheduleRequest);
    List<String> medicinesFor(String pillRegimenId, String dosageId);
    void updateDosageTaken(String pillRegimenId, String dosageId);
    String getPreviousDosage(String pillRegimenId, String dosageId);
    DateTime getNextDosageTime(String pillRegimenId, String dosageId);
}
