package org.motechproject.server.pillreminder.domain;

import org.junit.Test;
import org.motechproject.server.pillreminder.util.TestUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DosageTest {

    @Test
    public void shouldTestEquality() {
        Set<Medicine> medicines = new HashSet<Medicine>();
        medicines.add(new Medicine("m1"));
        medicines.add(new Medicine("m2"));

        Set<Reminder> reminders = new HashSet<Reminder>();
        Reminder reminder1 = new Reminder(2,30,4,30);
        Reminder reminder2 = new Reminder(3,30,4,30);
        reminders.add(reminder1);
        reminders.add(reminder2);

        Dosage dosage = new Dosage(medicines, reminders);

        assertFalse(dosage.equals(""));
        assertFalse(dosage.equals(null));
        assertFalse(dosage.equals(new Dosage()));

        assertTrue(dosage.equals(dosage));
        assertTrue(dosage.equals(new Dosage(medicines, reminders)));
    }
}
