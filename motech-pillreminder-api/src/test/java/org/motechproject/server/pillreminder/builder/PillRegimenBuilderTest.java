package org.motechproject.server.pillreminder.builder;

import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.Medicine;
import org.motechproject.server.pillreminder.domain.PillRegimen;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.motechproject.server.pillreminder.util.Util.newDate;

public class PillRegimenBuilderTest {

    private PillRegimenBuilder builder = new PillRegimenBuilder();

    @Test
    public void shouldCreateAPillRegimenFromARequest() {
        Date startDate = newDate(2011, 5, 20);
        Date endDate = newDate(2011, 5, 21);
        String externalId = "123";

        MedicineRequest medicineRequest = new MedicineRequest("m1", startDate, endDate);
        List<MedicineRequest> medicineRequests = asList(medicineRequest);

        DosageRequest dosageRequest = new DosageRequest(10, 5, medicineRequests);
        PillRegimenRequest pillRegimenRequest = new PillRegimenRequest(externalId, 5, 20, asList(dosageRequest));

        PillRegimen pillRegimen = builder.createFrom(pillRegimenRequest);

        assertEquals(externalId, pillRegimen.getExternalId());
        assertEquals(5, pillRegimen.getReminderRepeatWindowInHours());
        assertEquals(20, pillRegimen.getReminderRepeatIntervalInMinutes());
        assertEquals(1, pillRegimen.getDosages().size());
        for (Dosage dosage : pillRegimen.getDosages()) {
            assertEquals(new Time(10, 5), dosage.getDosageTime());
            assertEquals(1, dosage.getMedicines().size());
            for (Medicine medicine : dosage.getMedicines()) {
                assertEquals("m1", medicine.getName());
                assertEquals(startDate, medicine.getStartDate());
                assertEquals(endDate, medicine.getEndDate());
            }
        }
    }
}
