package org.motechproject.server.pillreminder.builder;

import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.contract.ReminderRequest;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.junit.Test;
import org.motechproject.server.pillreminder.domain.Medicine;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.motechproject.server.pillreminder.domain.Reminder;
import org.motechproject.server.pillreminder.util.TestUtil;

import java.util.*;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.motechproject.server.pillreminder.util.TestUtil.newDate;

public class PillRegimenBuilderTest {

    private PillRegimenBuilder builder = new PillRegimenBuilder();

    @Test
    public void shouldCreateAPillRegimenFromARequest() {
        Date date1 = newDate(2011, 5, 20);
        Date date2 = newDate(2011, 5, 21);
        String externalId = "123";

        List<String> medicineRequests = asList("m1");
        List<ReminderRequest> reminderRequests = asList(new ReminderRequest(1, 30, 5, 300));
        DosageRequest dosageRequest = new DosageRequest(medicineRequests, reminderRequests);
        PillRegimenRequest pillRegimenRequest = new PillRegimenRequest(externalId, date1, date2, asList(dosageRequest));

        Set<Medicine> expectedMedicines = new HashSet<Medicine>();
        expectedMedicines.add(new Medicine("m1"));

        Set<Reminder> expectedReminders = new HashSet<Reminder>();
        Reminder reminder = new Reminder(1, 30, 5, 300);
        expectedReminders.add(reminder);

        Set<Dosage> expectedDosages = new HashSet<Dosage>();
        Dosage expectedDosage = new Dosage(expectedMedicines, expectedReminders);
        expectedDosages.add(expectedDosage);

        PillRegimen pillRegimen = builder.createFrom(pillRegimenRequest);

        PillRegimen expectedPillRegimen = new PillRegimen(externalId, date1, date2, expectedDosages);
        assertEquals(expectedPillRegimen, pillRegimen);
    }
}
