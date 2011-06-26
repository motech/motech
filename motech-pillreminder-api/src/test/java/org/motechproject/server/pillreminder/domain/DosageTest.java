package org.motechproject.server.pillreminder.domain;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DosageTest {

    @Test
    public void shouldTestAccessors(){
        Dosage dosage = new Dosage();
        Set<Medicine> medicines = new HashSet<Medicine>();
        Set<Reminder> reminders = new HashSet<Reminder>();

        dosage.setMedicines(medicines);
        assertEquals(medicines,dosage.getMedicines());
        dosage.setReminders(reminders);
        assertEquals(reminders,dosage.getReminders());
    }
}
