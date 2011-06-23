package com.motechproject.server.pillreminder.builder;

import com.motechproject.server.pillreminder.contract.DosageRequest;
import com.motechproject.server.pillreminder.domain.Dosage;
import com.motechproject.server.pillreminder.domain.Medicine;
import com.motechproject.server.pillreminder.domain.Reminder;
import org.junit.Test;

import java.util.*;

import static com.motechproject.server.pillreminder.util.TestUtil.getDate;
import static org.junit.Assert.assertEquals;

public class DosageBuilderTest {

    private DosageBuilder builder = new DosageBuilder();

    @Test
    public void shouldBuildADosageFromRequest(){
        List<String> medicines = Arrays.asList("m1", "m2");
        Date date1 = getDate(2011, 5, 20);
        Date date2 = getDate(2011, 5, 21);
        List<Date> reminders = Arrays.asList(date1, date2);
        DosageRequest dosageRequest = new DosageRequest(medicines, reminders);

        Dosage dosage = builder.createFrom(dosageRequest);

        HashSet<Medicine> expectedMedicines = new HashSet<Medicine>();
        expectedMedicines.add(new Medicine("m1"));
        expectedMedicines.add(new Medicine("m2"));

        HashSet<Reminder> expectedReminders = new HashSet<Reminder>();
        expectedReminders.add(new Reminder(date1));
        expectedReminders.add(new Reminder(date2));

        Dosage expectedDosage = new Dosage(expectedMedicines, expectedReminders);
        assertEquals(expectedDosage, dosage);
    }

}
