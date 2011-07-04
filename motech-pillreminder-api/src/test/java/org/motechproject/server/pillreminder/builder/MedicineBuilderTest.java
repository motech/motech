package org.motechproject.server.pillreminder.builder;

import org.junit.Test;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.server.pillreminder.domain.Medicine;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.motechproject.server.pillreminder.util.Util.getDateAfter;

/**
 * Created by IntelliJ IDEA.
 * User: prateekk
 * Date: 7/1/11
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class MedicineBuilderTest {
    private MedicineBuilder builder = new MedicineBuilder();

    @Test
    public void shouldBuildADosageFromRequest() {
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
