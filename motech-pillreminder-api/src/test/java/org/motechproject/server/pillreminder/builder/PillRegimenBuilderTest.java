package org.motechproject.server.pillreminder.builder;

import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.junit.Test;
import org.motechproject.server.pillreminder.domain.Medicine;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.motechproject.server.pillreminder.domain.Reminder;
import org.motechproject.server.pillreminder.util.TestUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class PillRegimenBuilderTest {

    private PillRegimenBuilder builder = new PillRegimenBuilder();

    @Test
    public void shouldCreateAPillRegimenFromARequest() {
        Date date1 = TestUtil.newDate(2011, 5, 20);
        Date date2 = TestUtil.newDate(2011, 5, 21);
        String externalId = "123";
        String medicine1Name = "m1";
        String medicine2Name = "m2";
        DosageRequest dosageRequest = new DosageRequest(Arrays.asList(medicine1Name, medicine2Name), Arrays.asList(date1, date2));
        PillRegimenRequest pillRegimenRequest = new PillRegimenRequest(externalId, date1, date2, Arrays.asList(dosageRequest));

        PillRegimen pillRegimen = builder.createFrom(pillRegimenRequest);

        Set<Dosage> expectedDosages = new HashSet<Dosage>();
        Set<Medicine> expectedMedicines = new HashSet<Medicine>();
        expectedMedicines.add(new Medicine(medicine1Name));
        expectedMedicines.add(new Medicine(medicine2Name));

        Set<Reminder> expectedReminders = new HashSet<Reminder>();
        expectedReminders.add(new Reminder(date1));
        expectedReminders.add(new Reminder(date2));

        Dosage expectedDosage = new Dosage(expectedMedicines, expectedReminders);
        expectedDosages.add(expectedDosage);

        PillRegimen expectedPillRegimen = new PillRegimen(externalId, date1, date2, expectedDosages);
        assertEquals(expectedPillRegimen,pillRegimen);
    }
}
