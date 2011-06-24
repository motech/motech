package com.motechproject.server.pillreminder.service;

import com.motechproject.server.pillreminder.builder.PillRegimenBuilder;
import com.motechproject.server.pillreminder.contract.PillRegimenRequest;
import com.motechproject.server.pillreminder.domain.PillRegimen;
import com.motechproject.server.pillreminder.repository.AllPillRegimens;

public class PillReminderServiceImpl implements PillReminderService {

    private AllPillRegimens allPillRegimens;

    public PillReminderServiceImpl(AllPillRegimens allPillRegimens) {
        this.allPillRegimens = allPillRegimens;
    }

    @Override
    public void createNew(PillRegimenRequest pillRegimenRequest) {
        PillRegimenBuilder builder = new PillRegimenBuilder();
        PillRegimen pillRegimen = builder.createFrom(pillRegimenRequest);
        pillRegimen.validate();
        allPillRegimens.add(pillRegimen);
    }

}
