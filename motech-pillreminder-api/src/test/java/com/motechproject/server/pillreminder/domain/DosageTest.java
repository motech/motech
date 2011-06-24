package com.motechproject.server.pillreminder.domain;

import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.motechproject.server.pillreminder.util.TestUtil.newDate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DosageTest {

    @Test
    public void shouldTestEquality() {
        Date date1 = newDate(2011, 5, 20);
        Date date2 = newDate(2011, 5, 21);

        Set<Medicine> expectedMedicines = new HashSet<Medicine>();
        expectedMedicines.add(new Medicine("m1"));
        expectedMedicines.add(new Medicine("m2"));

        Set<Reminder> expectedReminders = new HashSet<Reminder>();
        expectedReminders.add(new Reminder(date1));
        expectedReminders.add(new Reminder(date2));

        Dosage dosage = new Dosage(expectedMedicines, expectedReminders);

        assertFalse(dosage.equals(""));
        assertFalse(dosage.equals(null));
        assertFalse(dosage.equals(new Dosage()));

        assertTrue(dosage.equals(dosage));
        assertTrue(dosage.equals(new Dosage(expectedMedicines, expectedReminders)));
    }
}
