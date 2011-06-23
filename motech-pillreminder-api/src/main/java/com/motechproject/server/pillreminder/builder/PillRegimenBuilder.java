package com.motechproject.server.pillreminder.builder;

import com.motechproject.server.pillreminder.contract.DosageRequest;
import com.motechproject.server.pillreminder.contract.PillRegimenRequest;
import com.motechproject.server.pillreminder.domain.Dosage;
import com.motechproject.server.pillreminder.domain.PillRegimen;

import java.util.HashSet;
import java.util.Set;

public class PillRegimenBuilder {

    private DosageBuilder dosageBuilder = new DosageBuilder();

    public PillRegimen createFrom(PillRegimenRequest pillRegimenRequest) {
        Set<Dosage> dosages = new HashSet<Dosage>();
        for (DosageRequest dosageRequest : pillRegimenRequest.getDosageContracts()) {
            Dosage dosage = dosageBuilder.createFrom(dosageRequest);
            dosages.add(dosage);
        }
        return new PillRegimen(pillRegimenRequest.getExternalId(), pillRegimenRequest.getStartDate(), pillRegimenRequest.getEndDate(), dosages);
    }
}
