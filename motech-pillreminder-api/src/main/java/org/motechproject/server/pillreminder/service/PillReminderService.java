package org.motechproject.server.pillreminder.service;

import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;

public interface PillReminderService {
    void createNew(PillRegimenRequest newScheduleRequest);
    void renew(PillRegimenRequest newScheduleRequest);
    void stopTodaysReminders(String pillRegimenId, String dosageId);
    PillRegimenResponse getPillRegimen(String externalId);
}
