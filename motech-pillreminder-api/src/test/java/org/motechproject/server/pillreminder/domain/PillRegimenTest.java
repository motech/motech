package org.motechproject.server.pillreminder.domain;

import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.model.Time;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.motechproject.server.pillreminder.util.Util.newDate;

public class PillRegimenTest {

    @Test(expected = ValidationException.class)
    public void shouldNotValidateIfEndDateIsBeforeTheStartDate() {
        Date startDate = newDate(2011, 1, 1);
        Date endDate = newDate(2011, 0, 1);

        Set<Medicine> medicines = new HashSet<Medicine>();
        medicines.add(new Medicine("m1", startDate, endDate));

        Set<Dosage> dosages = new HashSet<Dosage>();
        dosages.add(new Dosage(new Time(9, 5), medicines));

        PillRegimen regimen = new PillRegimen("1", 5, 10, dosages);
        regimen.validate();
    }

    @Test
    public void shouldValidateIfNoEndDateIsProvided() {
        Date startDate = newDate(2011, 1, 1);
        Date endDate = null;

        Set<Medicine> medicines = new HashSet<Medicine>();
        medicines.add(new Medicine("m1", startDate, endDate));

        Set<Dosage> dosages = new HashSet<Dosage>();
        dosages.add(new Dosage(new Time(9, 5), medicines));

        PillRegimen regimen = new PillRegimen("1", 5, 10, dosages);

        try {
            regimen.validate();
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void shouldTestAccessors() {
        PillRegimen regimen = new PillRegimen();

        regimen.setExternalId("123");
        assertEquals("123", regimen.getExternalId());

        Set<Dosage> dosages = new HashSet<Dosage>();
        regimen.setDosages(dosages);
        assertEquals(dosages, regimen.getDosages());

        regimen.setType("type");
        assertEquals("type", regimen.getType());

    }

    @Test
    public void shouldFindDosageById() {
        Dosage dosage1 = new Dosage();
        dosage1.setId("1");
        Dosage dosage2 = new Dosage();
        dosage2.setId("2");

        Set<Dosage> dosages = new HashSet<Dosage>();
        dosages.add(dosage1);
        dosages.add(dosage2);

        PillRegimen regimen = new PillRegimen();
        regimen.setDosages(dosages);

        assertEquals(dosage1, regimen.getDosage("1"));
        assertEquals(dosage2, regimen.getDosage("2"));
    }
}
