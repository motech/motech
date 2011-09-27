package org.motechproject.server.pillreminder.builder;

import org.motechproject.server.pillreminder.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.PillRegimen;

import java.util.HashSet;
import java.util.Set;

public class PillRegimenBuilder {

    private DosageBuilder dosageBuilder = new DosageBuilder();

    public PillRegimen createFrom(DailyPillRegimenRequest dailyPillRegimenRequest) {
        Set<Dosage> dosages = new HashSet<Dosage>();
        for (DosageRequest dosageRequest : dailyPillRegimenRequest.getDosageRequests())
            dosages.add(dosageBuilder.createFrom(dosageRequest));
        return new PillRegimen(dailyPillRegimenRequest.getExternalId(), dailyPillRegimenRequest.getPillWindowInHours(), dailyPillRegimenRequest.getReminderRepeatIntervalInMinutes(), dosages);
    }
}
