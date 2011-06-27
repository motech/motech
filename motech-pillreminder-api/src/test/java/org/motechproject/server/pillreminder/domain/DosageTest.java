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

        dosage.setMedicines(medicines);
        dosage.setStartHour(9);
        dosage.setStartMinute(5);
        assertEquals(medicines, dosage.getMedicines());
        assertEquals(new Integer(9),dosage.getStartHour());
        assertEquals(new Integer(5),dosage.getStartMinute());
    }
}
