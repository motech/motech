package org.motechproject.server.pillreminder.api.builder;

import junit.framework.Assert;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.server.pillreminder.api.contract.DosageResponse;
import org.motechproject.server.pillreminder.api.contract.MedicineResponse;
import org.motechproject.server.pillreminder.api.domain.Dosage;
import org.motechproject.server.pillreminder.api.domain.Medicine;

import java.util.HashSet;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;

public class DosageResponseBuilderTest {

    @Test
    public void shouldConstructADosageResponseGivenADosage() {
        LocalDate date = DateUtil.today();
        HashSet<Medicine> medicines = new HashSet<Medicine>();
        medicines.add(new Medicine("medicine1", date, date));
        medicines.add(new Medicine("medicine2", date, date));
        Dosage dosage = new Dosage(new Time(10, 5), medicines);
        dosage.setId("dosageId");

        DosageResponse dosageResponse = new DosageResponseBuilder().createFrom(dosage);

        Assert.assertEquals("dosageId", dosageResponse.getDosageId());
        Assert.assertEquals(10, dosageResponse.getDosageHour());
        Assert.assertEquals(5, dosageResponse.getDosageMinute());

        List<MedicineResponse> dosageResponseMedicines = dosageResponse.getMedicines();
        Assert.assertEquals(asList("medicine1", "medicine2"), extract(dosageResponseMedicines, on(MedicineResponse.class).getName()));
    }
}
