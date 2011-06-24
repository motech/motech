package org.motechproject.server.pillreminder.builder;

import org.motechproject.server.pillreminder.contract.DosageRequest;
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
    public void shouldBuildADosageFromRequest(){
        List<String> medicines = Arrays.asList("m1", "m2");
        Date date1 = newDate(2011, 5, 20);
        Date date2 = newDate(2011, 5, 21);
        List<Date> reminders = Arrays.asList(date1, date2);
        DosageRequest dosageRequest = new DosageRequest(medicines, reminders);

        Dosage dosage = builder.createFrom(dosageRequest);

        Set<Medicine> expectedMedicines = new HashSet<Medicine>();
        expectedMedicines.add(new Medicine("m1"));
        expectedMedicines.add(new Medicine("m2"));

        Set<Reminder> expectedReminders = new HashSet<Reminder>();
        expectedReminders.add(new Reminder(date1));
        expectedReminders.add(new Reminder(date2));

        Dosage expectedDosage = new Dosage(expectedMedicines, expectedReminders);
        assertEquals(expectedDosage, dosage);
    }

}
