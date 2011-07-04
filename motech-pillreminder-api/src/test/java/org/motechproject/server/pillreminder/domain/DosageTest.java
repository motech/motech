package org.motechproject.server.pillreminder.domain;

import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.builder.MedicineBuilder;
import org.motechproject.server.pillreminder.contract.MedicineRequest;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DosageTest {

    @Test
    public void shouldGetStartDateWhichIsTheEarliestStartDateOfItsMedicines(){
        Dosage dosage = new Dosage();
        Set<Medicine> medicines = new HashSet<Medicine>();
        medicines.add(new MedicineBuilder().createFrom(new MedicineRequest("medicine1", new Date(2010, 10, 10), new Date(2011, 10, 10))));
        medicines.add(new MedicineBuilder().createFrom(new MedicineRequest("medicine2", new Date(2010, 11, 11), new Date(2011, 11, 11))));

        dosage.setMedicines(medicines);
        assertEquals(new Date(2010, 10, 10), dosage.getStartDate());
    }

    @Test
    public void shouldGetEndDateWhichIsTheLatestEndDateOfItsMedicines(){
        Dosage dosage = new Dosage();
        Set<Medicine> medicines = new HashSet<Medicine>();
        medicines.add(new MedicineBuilder().createFrom(new MedicineRequest("medicine1", new Date(2010, 10, 10), new Date(2011, 10, 10))));
        medicines.add(new MedicineBuilder().createFrom(new MedicineRequest("medicine2", new Date(2010, 11, 11), new Date(2011, 11, 11))));

        dosage.setMedicines(medicines);
        assertEquals(new Date(2011, 11, 11), dosage.getEndDate());
    }
}
