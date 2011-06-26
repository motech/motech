package org.motechproject.server.pillreminder.builder;

import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.PillRegimen;

import java.util.HashSet;
import java.util.Set;

public class PillRegimenBuilder {

    private DosageBuilder dosageBuilder = new DosageBuilder();

    public PillRegimen createFrom(PillRegimenRequest pillRegimenRequest) {
        Set<Dosage> dosages = new HashSet<Dosage>();
        for (DosageRequest dosageRequest : pillRegimenRequest.getDosageContracts()) {
            dosages.add(dosageBuilder.createFrom(dosageRequest));
        }
        return new PillRegimen(pillRegimenRequest.getExternalId(), pillRegimenRequest.getStartDate(), pillRegimenRequest.getEndDate(), dosages);
    }
}
