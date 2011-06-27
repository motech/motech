package org.motechproject.server.pillreminder.domain;

import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.motechproject.server.pillreminder.util.TestUtil.areDatesSame;
import static org.motechproject.server.pillreminder.util.TestUtil.newDate;

public class PillRegimenTest {

    @Test(expected = ValidationException.class)
    public void shouldThrowAnExceptionIfValidationFails() {
        Date startDate = newDate(2011, 1, 1);
        Date endDate = newDate(2011, 0, 1);
        Set<Dosage> dosages = new HashSet<Dosage>();

        PillRegimen regimen = new PillRegimen("1", startDate, endDate, 5, 10, dosages);
        regimen.validate();
    }

    @Test
    public void shouldTestAccessors() {
        PillRegimen regimen = new PillRegimen();

        regimen.setExternalId("123");
        assertEquals("123",regimen.getExternalId());

        Set<Dosage> dosages = new HashSet<Dosage>();
        regimen.setDosages(dosages);
        assertEquals(dosages, regimen.getDosages());

        regimen.setStartDate(newDate(2011, 3, 1));
        assertTrue(areDatesSame(newDate(2011, 3, 1), regimen.getStartDate()));

        regimen.setEndDate(newDate(2011, 4, 1));
        assertTrue(areDatesSame(newDate(2011, 4, 1), regimen.getEndDate()));

        regimen.setType("type");
        assertEquals("type",regimen.getType());

    }

}
