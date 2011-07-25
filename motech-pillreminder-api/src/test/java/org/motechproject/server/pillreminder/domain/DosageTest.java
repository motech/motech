package org.motechproject.server.pillreminder.domain;

import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.util.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DosageTest {

    @Test
    public void shouldGetStartDateWhichIsTheEarliestStartDateOfItsMedicines(){
        Set<Medicine> medicines = new HashSet<Medicine>();
        medicines.add(new Medicine("medicine1", new Date(2010, 10, 10), new Date(2011, 10, 10)));
        medicines.add(new Medicine("medicine2", new Date(2010, 11, 11), new Date(2011, 11, 11)));
        Dosage dosage = new Dosage(new Time(9, 5), medicines);

        assertEquals(new Date(2010, 10, 10), dosage.getStartDate());
    }

    @Test
    public void shouldGetEndDateWhichIsTheLatestEndDateOfItsMedicines(){
        Set<Medicine> medicines = new HashSet<Medicine>();
        medicines.add(new Medicine("medicine1", new Date(2010, 10, 10), new Date(2011, 10, 10)));
        medicines.add(new Medicine("medicine2", new Date(2010, 11, 11), new Date(2011, 11, 11)));
        Dosage dosage = new Dosage(new Time(9, 5), medicines);

        assertEquals(new Date(2011, 11, 11), dosage.getEndDate());
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
    public void shouldGetIsTakenOnADosage() {
        Set<Medicine> medicines = new HashSet<Medicine>();
        Date today = new Date();
        Time nineIneTheMorning = new Time(9, 0);

        medicines.add(new Medicine("medicine1", today, Util.getDateAfter(today, 2)));
        Dosage dosage = new Dosage(nineIneTheMorning, medicines);

        assertTrue(dosage.isTaken(nineIneTheMorning));
    }
}
