package com.motechproject.server.pillreminder.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MedicineTest {

    @Test
    public void shouldTestEquality(){
        Medicine medicine = new Medicine("m1");

        assertFalse(medicine.equals(null));
        assertFalse(medicine.equals(""));
        assertFalse(medicine.equals(new Medicine()));
        assertFalse(medicine.equals(new Medicine("m2")));

        assertTrue(medicine.equals(medicine));
        assertTrue(medicine.equals(new Medicine("m1")));
    }
}
