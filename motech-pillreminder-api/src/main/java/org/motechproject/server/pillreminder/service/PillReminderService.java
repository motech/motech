package org.motechproject.server.pillreminder.service;

import org.motechproject.server.pillreminder.contract.PillRegimenRequest;

import java.util.List;

public interface PillReminderService {
    void createNew(PillRegimenRequest newScheduleRequest);
    void renew(PillRegimenRequest newScheduleRequest);
    List<String> medicinesFor(String pillRegimenId, String dosageId);
    void updateDosageTaken(String pillRegimenId, String dosageId);
}
