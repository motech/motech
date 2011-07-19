package org.motechproject.server.pillreminder.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.Medicine;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.motechproject.server.pillreminder.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.motechproject.server.pillreminder.util.Util.getDateAfter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationPillReminderAPI.xml"})
public class AllPillRegimensIT {

    @Autowired
    private AllPillRegimens allPillRegimens;

    private Date startDate;
    private Date endDate;

    @Before
    public void setUp() {
        startDate = Util.newDate(2011, 1, 1);
        endDate = Util.newDate(2011, 3, 1);
    }

    @Test
    public void shouldSaveThePillRegimenWithoutDosage() {
        PillRegimen pillRegimen = new PillRegimen("1234", 5, 20, null);

        allPillRegimens.add(pillRegimen);

        assertNotNull(pillRegimen.getId());
        allPillRegimens.remove(pillRegimen);
    }

    @Test
    public void shouldSaveThePillRegimenWithDosages() {
        Medicine medicine = new Medicine("m1", startDate, endDate);
        Medicine medicine2 = new Medicine("m2", startDate, getDateAfter(startDate, 3));
        Set<Medicine> medicines = new HashSet<Medicine>();
        medicines.add(medicine);
        medicines.add(medicine2);

        Dosage dosage = new Dosage(new Time(9, 5), medicines);
        Set<Dosage> dosages = new HashSet<Dosage>();
        dosages.add(dosage);

        PillRegimen pillRegimen = new PillRegimen("1234", 5, 20, dosages);
        allPillRegimens.add(pillRegimen);

        assertNotNull(pillRegimen.getId());

        PillRegimen pillRegimenFromDB = allPillRegimens.get(pillRegimen.getId());
        assertEquals(5, pillRegimenFromDB.getReminderRepeatWindowInHours());
        assertEquals(20, pillRegimenFromDB.getReminderRepeatIntervalInMinutes());

        Object[] dosagesFromDB = pillRegimenFromDB.getDosages().toArray();
        assertEquals(1, dosagesFromDB.length);

        Set<Medicine> medicinesFromDB = ((Dosage)dosagesFromDB[0]).getMedicines();
        assertEquals(2, medicinesFromDB.toArray().length);

        allPillRegimens.remove(pillRegimen);
    }

    @Test
    public void shouldGetPillRegimenByExternalId() {
        PillRegimen pillRegimen = new PillRegimen("1234", 5, 20, null);
        allPillRegimens.add(pillRegimen);
        PillRegimen returnedRegimen = allPillRegimens.findByExternalId("1234");
        assertNotNull(returnedRegimen);
        assertEquals(returnedRegimen.getExternalId(), "1234");
    }
}
