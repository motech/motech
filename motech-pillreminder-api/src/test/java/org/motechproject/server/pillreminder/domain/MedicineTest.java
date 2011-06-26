package org.motechproject.server.pillreminder.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MedicineTest {

    @Test
    public void shouldTestEquality() {
        Medicine medicine = new Medicine("m1");

        assertFalse(medicine.equals(null));
        assertFalse(medicine.equals(""));
        assertFalse(medicine.equals(new Medicine()));
        assertFalse(medicine.equals(new Medicine("m2")));

        assertTrue(medicine.equals(medicine));
        assertTrue(medicine.equals(new Medicine("m1")));
    }

    @Test
    public void shouldTestAccessors() {
        Medicine medicine = new Medicine("m1");
        medicine.setName("m2");
        assertEquals("m2", medicine.getName());
    }
}
