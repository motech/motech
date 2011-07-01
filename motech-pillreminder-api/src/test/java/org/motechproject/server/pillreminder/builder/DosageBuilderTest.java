package org.motechproject.server.pillreminder.builder;

import org.junit.Test;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.Medicine;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.motechproject.server.pillreminder.util.Util.getEndDateAfter;

public class DosageBuilderTest {

    private DosageBuilder builder = new DosageBuilder();

    @Test
    public void shouldBuildADosageFromRequest() {
        Date startDate = new Date();
        Date endDate = getEndDateAfter(startDate, 2);
        MedicineRequest medicineRequest = new MedicineRequest("m1", startDate, endDate);

        DosageRequest dosageRequest = new DosageRequest(9, 5, Arrays.asList(medicineRequest));

        Dosage dosage = builder.createFrom(dosageRequest);

        assertEquals(9, dosage.getStartHour());
        assertEquals(5, dosage.getStartMinute());
        assertEquals(1, dosage.getMedicines().size());
        for (Medicine medicine : dosage.getMedicines()) {
            assertEquals("m1", medicine.getName());
            assertEquals(startDate, medicine.getStartDate());
            assertEquals(endDate, medicine.getEndDate());
        }
    }
}
