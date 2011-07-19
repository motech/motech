package org.motechproject.server.pillreminder.service;

import org.motechproject.server.pillreminder.contract.PillRegimenRequest;

public interface PillReminderService {
    void createNew(PillRegimenRequest newScheduleRequest);
    void renew(PillRegimenRequest newScheduleRequest);
}
