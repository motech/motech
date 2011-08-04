package org.motechproject.server.pillreminder.builder;

import org.junit.Test;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.server.pillreminder.domain.Medicine;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.motechproject.server.pillreminder.util.Util.getDateAfter;

public class MedicineBuilderTest {
    private MedicineBuilder builder = new MedicineBuilder();

    @Test
    public void shouldBuildAMedicineFromRequest() {
        String medicineName = "paracetamol";
        Date startDate = new Date();
        Date endDate = getDateAfter(startDate, 2);
        MedicineRequest medicineRequest = new MedicineRequest(medicineName, startDate, endDate);

        Medicine medicine = builder.createFrom(medicineRequest);

        assertEquals(startDate, medicine.getStartDate());
        assertEquals(endDate, medicine.getEndDate());
        assertEquals(medicineName, medicine.getName());
    }
}
