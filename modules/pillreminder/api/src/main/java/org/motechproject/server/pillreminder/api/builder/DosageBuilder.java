package org.motechproject.server.pillreminder.api.builder;

import org.motechproject.commons.date.model.Time;
import org.motechproject.server.pillreminder.api.contract.DosageRequest;
import org.motechproject.server.pillreminder.api.contract.MedicineRequest;
import org.motechproject.server.pillreminder.api.domain.Dosage;
import org.motechproject.server.pillreminder.api.domain.Medicine;

import java.util.HashSet;
import java.util.Set;

public class DosageBuilder {

    public Dosage createFrom(DosageRequest dosageRequest) {
        Set<Medicine> medicines = new HashSet<Medicine>();
        Time dosageTime = new Time(dosageRequest.getStartHour(), dosageRequest.getStartMinute());
        for (MedicineRequest medicineRequest : dosageRequest.getMedicineRequests()) {
            medicines.add(new MedicineBuilder().createFrom(medicineRequest));
        }
        return new Dosage(dosageTime, medicines);
    }
}
