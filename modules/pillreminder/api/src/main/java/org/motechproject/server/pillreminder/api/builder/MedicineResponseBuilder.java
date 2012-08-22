package org.motechproject.server.pillreminder.api.builder;

import org.motechproject.server.pillreminder.api.contract.MedicineResponse;
import org.motechproject.server.pillreminder.api.domain.Medicine;

public class MedicineResponseBuilder {
    public MedicineResponse createFrom(Medicine medicine) {
        return new MedicineResponse(medicine.getName(), medicine.getStartDate(), medicine.getEndDate());
    }
}
