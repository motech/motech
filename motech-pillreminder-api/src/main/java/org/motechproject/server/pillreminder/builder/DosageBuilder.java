package org.motechproject.server.pillreminder.builder;

import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.Medicine;

import java.util.HashSet;
import java.util.Set;

public class DosageBuilder {

    public Dosage createFrom(DosageRequest dosageRequest) {
        Set<Medicine> medicines = new HashSet<Medicine>();
        for (String medicineName : dosageRequest.getMedicineRequests())
            medicines.add(new Medicine(medicineName));
        return new Dosage(dosageRequest.getStartHour(), dosageRequest.getStartMinute(), medicines);
    }
}
