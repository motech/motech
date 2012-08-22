package org.motechproject.server.pillreminder.api.builder;

import org.motechproject.server.pillreminder.api.contract.MedicineRequest;
import org.motechproject.server.pillreminder.api.domain.Medicine;
public class MedicineBuilder {
    public Medicine createFrom(MedicineRequest medicineRequest) {
        return new Medicine(medicineRequest.getName(), medicineRequest.getStartDate(), medicineRequest.getEndDate());
    }
}
