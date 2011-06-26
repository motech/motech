package org.motechproject.server.pillreminder.builder;

import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.ReminderRequest;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.junit.Test;
import org.motechproject.server.pillreminder.domain.Medicine;
import org.motechproject.server.pillreminder.domain.Reminder;

import java.util.*;

import static org.motechproject.server.pillreminder.util.TestUtil.newDate;
import static org.junit.Assert.assertEquals;

public class DosageBuilderTest {

    private DosageBuilder builder = new DosageBuilder();

    @Test
    public void shouldBuildADosageFromRequest() {
        ReminderRequest reminderRequest = new ReminderRequest(1, 30, 5, 300);
        DosageRequest dosageRequest = new DosageRequest(Arrays.asList("m1"), Arrays.asList(reminderRequest));

        Set<Medicine> expectedMedicines = new HashSet<Medicine>();
        expectedMedicines.add(new Medicine("m1"));

        Set<Reminder> expectedReminders = new HashSet<Reminder>();
        expectedReminders.add(new Reminder(1, 30, 5, 300));

        Dosage dosage = builder.createFrom(dosageRequest);

        Dosage expectedDosage = new Dosage(expectedMedicines, expectedReminders);
        assertEquals(expectedDosage, dosage);
    }

}
