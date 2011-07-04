package org.motechproject.server.pillreminder.builder;

import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.Medicine;

import java.util.HashSet;
import java.util.Set;

public class DosageBuilder {

    public Dosage createFrom(DosageRequest dosageRequest) {
        Set<Medicine> medicines = new HashSet<Medicine>();
        for (MedicineRequest medicineRequest : dosageRequest.getMedicineRequests()) {
            Medicine medicine = new Medicine(medicineRequest.getName(), medicineRequest.getStartDate(), medicineRequest.getEndDate());
            medicines.add(medicine);
        }
        return new Dosage(new Time(dosageRequest.getStartHour(), dosageRequest.getStartMinute()), medicines);
    }
}
