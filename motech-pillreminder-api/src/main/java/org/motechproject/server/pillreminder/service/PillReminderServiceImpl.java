package org.motechproject.server.pillreminder.service;

import org.motechproject.server.pillreminder.builder.PillRegimenBuilder;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.motechproject.server.pillreminder.dao.AllPillRegimens;

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
