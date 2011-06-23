package com.motechproject.server.pillreminder.builder;

import com.motechproject.server.pillreminder.contract.DosageRequest;
import com.motechproject.server.pillreminder.contract.PillRegimenRequest;
import com.motechproject.server.pillreminder.domain.Dosage;
import com.motechproject.server.pillreminder.domain.Medicine;
import com.motechproject.server.pillreminder.domain.PillRegimen;
import com.motechproject.server.pillreminder.domain.Reminder;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.motechproject.server.pillreminder.util.TestUtil.getDate;
import static org.junit.Assert.assertEquals;

public class PillRegimenBuilderTest {

    private PillRegimenBuilder builder = new PillRegimenBuilder();

    @Test
    public void shouldCreateAPillRegimenFromARequest() {
        Date date1 = getDate(2011, 5, 20);
        Date date2 = getDate(2011, 5, 21);
        String externalId = "123";
        String medicine1Name = "m1";
        String medicine2Name = "m2";
        DosageRequest dosageRequest = new DosageRequest(Arrays.asList(medicine1Name, medicine2Name), Arrays.asList(date1, date2));
        PillRegimenRequest pillRegimenRequest = new PillRegimenRequest(externalId, date1, date2, Arrays.asList(dosageRequest));

        PillRegimen pillRegimen = builder.createFrom(pillRegimenRequest);

        Set<Dosage> expectedDosages = new HashSet<Dosage>();
        HashSet<Medicine> expectedMedicines = new HashSet<Medicine>();
        expectedMedicines.add(new Medicine(medicine1Name));
        expectedMedicines.add(new Medicine(medicine2Name));

        HashSet<Reminder> expectedReminders = new HashSet<Reminder>();
        expectedReminders.add(new Reminder(date1));
        expectedReminders.add(new Reminder(date2));

        Dosage expectedDosage = new Dosage(expectedMedicines, expectedReminders);
        expectedDosages.add(expectedDosage);

        PillRegimen expectedPillRegimen = new PillRegimen(externalId, date1, date2, expectedDosages);
        assertEquals(expectedPillRegimen,pillRegimen);
    }
}
