package org.motechproject.server.pillreminder.builder;

import org.junit.Test;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.Medicine;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class DosageBuilderTest {

    private DosageBuilder builder = new DosageBuilder();

    @Test
    public void shouldBuildADosageFromRequest() {
        DosageRequest dosageRequest = new DosageRequest(9, 5, Arrays.asList("m1"));

        Dosage dosage = builder.createFrom(dosageRequest);

        assertEquals(new Integer(9), dosage.getStartHour());
        assertEquals(new Integer(5), dosage.getStartMinute());
        assertEquals(1, dosage.getMedicines().size());
        for (Medicine medicine : dosage.getMedicines()) {
            assertEquals("m1", medicine.getName());
        }
    }
}
