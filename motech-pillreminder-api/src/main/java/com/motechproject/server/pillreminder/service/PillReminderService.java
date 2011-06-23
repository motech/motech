package com.motechproject.server.pillreminder.service;

import com.motechproject.server.pillreminder.contract.PillRegimenRequest;

public interface PillReminderService {
    void createNew(PillRegimenRequest newScheduleRequest);
}
