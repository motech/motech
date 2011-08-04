package org.motechproject.server.pillreminder.builder;

import org.junit.Test;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.domain.Medicine;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.motechproject.server.pillreminder.util.Util.getDateAfter;

public class MedicineResponseBuilderTest {
    private MedicineResponseBuilder builder = new MedicineResponseBuilder();

    @Test
    public void shouldBuildMedicineResponse() {
        String medicineName = "paracetamol";
        Date startDate = new Date();
        Date endDate = getDateAfter(startDate, 2);
        Medicine medicine = new Medicine(medicineName, startDate, endDate);

        MedicineResponse medicineResponse = builder.createFrom(medicine);

        assertEquals(startDate, medicineResponse.getStartDate());
        assertEquals(endDate, medicineResponse.getEndDate());
        assertEquals(medicineName, medicineResponse.getName());
    }
}
