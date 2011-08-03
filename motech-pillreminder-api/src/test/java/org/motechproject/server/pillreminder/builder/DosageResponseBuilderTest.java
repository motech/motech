package org.motechproject.server.pillreminder.builder;

import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.Medicine;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class DosageResponseBuilderTest {

    @Test
    public void shouldConstructADosageResponseGivenADosage() {
        Date date = new Date();
        HashSet<Medicine> medicines = new HashSet<Medicine>();
        medicines.add(new Medicine("medicine1", date, date));
        medicines.add(new Medicine("medicine2", date, date));
        Dosage dosage = new Dosage(new Time(10, 05), medicines);
        dosage.setId("dosageId");

        DosageResponse dosageResponse = new DosageResponseBuilder().createFrom(dosage);

        Assert.assertEquals("dosageId", dosageResponse.getDosageId());
        Assert.assertEquals(10, dosageResponse.getStartHour());
        Assert.assertEquals(5, dosageResponse.getStartMinute());

        List<String> dosageResponseMedicines = dosageResponse.getMedicines();
        Assert.assertEquals(2, dosageResponseMedicines.size());
        Assert.assertTrue(dosageResponseMedicines.contains("medicine1"));
        Assert.assertTrue(dosageResponseMedicines.contains("medicine2"));
    }
}
