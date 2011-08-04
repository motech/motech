package org.motechproject.server.pillreminder.domain;

import org.junit.Test;
import org.motechproject.model.Time;

import java.util.*;

import static org.junit.Assert.*;

public class DosageTest {
    @Test
    public void shouldGetStartDateWhichIsTheEarliestStartDateOfItsMedicines() {
        Set<Medicine> medicines = new HashSet<Medicine>();
        medicines.add(new Medicine("medicine1", getDate(2010, 10, 10), getDate(2011, 10, 10)));
        medicines.add(new Medicine("medicine2", getDate(2010, 11, 11), getDate(2011, 11, 11)));
        Dosage dosage = new Dosage(new Time(9, 5), medicines);

        assertEquals(getDate(2010, 10, 10), dosage.getStartDate());
    }

    @Test
    public void shouldGetEndDateWhichIsTheLatestEndDateOfItsMedicines() {
        Set<Medicine> medicines = new HashSet<Medicine>();
        medicines.add(new Medicine("medicine1", getDate(2010, 10, 10), getDate(2011, 10, 10)));
        medicines.add(new Medicine("medicine2", getDate(2010, 11, 11), getDate(2011, 11, 11)));
        Dosage dosage = new Dosage(new Time(9, 5), medicines);

        assertEquals(getDate(2011, 11, 11), dosage.getEndDate());
    }

    @Test
    public void shouldGetNullEndDateIfAllTheMedicinesHaveNullEndDates(){
        Set<Medicine> medicines = new HashSet<Medicine>();
        medicines.add(new Medicine("medicine1", new Date(2010, 10, 10), null));
        medicines.add(new Medicine("medicine2", new Date(2010, 11, 11), null));
        Dosage dosage = new Dosage(new Time(9, 5), medicines);

        assertNull(dosage.getEndDate());
    }

    @Test
    public void shouldGetEndDateWhichIsTheLatestEndDateOfItsMedicinesWhenSomeMedicinesHaveNullEndDates(){
        Set<Medicine> medicines = new HashSet<Medicine>();
        medicines.add(new Medicine("medicine1", new Date(2010, 10, 10), new Date(2011, 10, 10)));
        medicines.add(new Medicine("medicine2", new Date(2010, 11, 11), new Date(2011, 11, 11)));
        medicines.add(new Medicine("medicine2", new Date(2010, 11, 11), null));
        medicines.add(new Medicine("medicine2", new Date(2010, 11, 11), null));
        Dosage dosage = new Dosage(new Time(9, 5), medicines);

        assertEquals(new Date(2011, 11, 11), dosage.getEndDate());
    }

    @Test
    public void shouldReturnMedicineNames() {
        Set<Medicine> medicines = new HashSet<Medicine>();
        medicines.add(new Medicine("medicine1", getDate(2010, 10, 10), getDate(2011, 10, 10)));
        medicines.add(new Medicine("medicine2", getDate(2010, 11, 11), getDate(2011, 11, 11)));
        Dosage dosage = new Dosage(new Time(9, 5), medicines);

        List<String> medicineNames = dosage.getMedicineNames();
        assertTrue(medicineNames.contains("medicine1"));
        assertTrue(medicineNames.contains("medicine2"));
    }

    private Date getDate(Integer year, Integer month, Integer day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        cal.clear(Calendar.MILLISECOND);
        return cal.getTime();
    }
}
