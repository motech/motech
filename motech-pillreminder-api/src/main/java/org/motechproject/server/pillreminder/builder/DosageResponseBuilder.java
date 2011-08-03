package org.motechproject.server.pillreminder.builder;

import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.domain.Dosage;

public class DosageResponseBuilder {

    public DosageResponse createFrom(Dosage dosage) {
        if (dosage == null) return null;
        return new DosageResponse(dosage.getStartTime().getHour(), dosage.getStartTime().getMinute(), dosage.getId(), dosage.getMedicineNames());
    }
}
