package com.motechproject.server.pillreminder.domain;

import org.junit.Test;
import org.springframework.test.annotation.ExpectedException;

import java.util.*;

import static com.motechproject.server.pillreminder.util.TestUtil.newDate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class PillRegimenTest {

    @Test
    public void shouldTestEquality() {
        Date date1 = mock(Date.class);
        Date date2 = mock(Date.class);
        Set<Dosage> dosages = new HashSet<Dosage>();

        PillRegimen regimen = new PillRegimen("1", date1, date2, dosages);

        assertFalse(regimen.equals(""));
        assertFalse(regimen.equals(null));
        assertFalse(regimen.equals(new PillRegimen("1", date2, date1, dosages)));
        assertFalse(regimen.equals(new PillRegimen("2", date2, date1, dosages)));

        assertTrue(regimen.equals(new PillRegimen("1", date1, date2, new HashSet<Dosage>())));
        assertTrue(regimen.equals(regimen));
        assertTrue(regimen.equals(new PillRegimen("1", date1, date2, dosages)));
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowAnExceptionIfValidationFails() {
        Date startDate = newDate(2011, 1, 1);
        Date endDate = newDate(2011, 0, 1);
        Set<Dosage> dosages = new HashSet<Dosage>();

        PillRegimen regimen = new PillRegimen("1", startDate, endDate, dosages);
        regimen.validate();
    }

}
