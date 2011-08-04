package org.motechproject.server.pillreminder.builder;

import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.PillRegimen;

import java.util.ArrayList;
import java.util.List;

public class PillRegimenResponseBuilder {

    private DosageResponseBuilder dosageResponseBuilder = new DosageResponseBuilder();

    public PillRegimenResponse createFrom(PillRegimen pillRegimen) {
        List<DosageResponse> dosages = new ArrayList<DosageResponse>();
        for (Dosage dosage : pillRegimen.getDosages())
            dosages.add(dosageResponseBuilder.createFrom(dosage));
        return new PillRegimenResponse(pillRegimen.getId(), pillRegimen.getExternalId(), pillRegimen.getReminderRepeatWindowInHours(), pillRegimen.getReminderRepeatIntervalInMinutes(), dosages);
    }
}
