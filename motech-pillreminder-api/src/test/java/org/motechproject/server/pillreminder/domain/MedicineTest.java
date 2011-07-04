package org.motechproject.server.pillreminder.domain;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.motechproject.server.pillreminder.util.Util.getDateAfter;

public class MedicineTest {

    @Test
    public void shouldTestEquality() {
        Date startDate = new Date();
        Date endDate = getDateAfter(startDate, 2);
        Medicine medicine = new Medicine("m1", startDate, endDate);

        assertFalse(medicine.equals(null));
        assertFalse(medicine.equals(""));
        assertFalse(medicine.equals(new Medicine()));
        assertFalse(medicine.equals(new Medicine("m2", startDate, endDate)));

        assertTrue(medicine.equals(medicine));
        assertTrue(medicine.equals(new Medicine("m1", startDate, endDate)));

        //Important case: medicine objects are same if they have the same names
        assertTrue(medicine.equals(new Medicine("m1", startDate, getDateAfter(startDate, 5))));
    }
}
