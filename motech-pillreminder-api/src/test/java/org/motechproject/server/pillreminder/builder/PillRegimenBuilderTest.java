package org.motechproject.server.pillreminder.builder;

import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.contract.ReminderRequest;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.junit.Test;
import org.motechproject.server.pillreminder.domain.Medicine;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.motechproject.server.pillreminder.domain.Reminder;
import org.motechproject.server.pillreminder.util.TestUtil;

import java.util.*;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.motechproject.server.pillreminder.util.TestUtil.newDate;

public class PillRegimenBuilderTest {

    private PillRegimenBuilder builder = new PillRegimenBuilder();

    @Test
    public void shouldCreateAPillRegimenFromARequest() {
        Date date1 = newDate(2011, 5, 20);
        Date date2 = newDate(2011, 5, 21);
        String externalId = "123";

        List<String> medicineRequests = asList("m1");
        List<ReminderRequest> reminderRequests = asList(new ReminderRequest(1, 30, 5, 300));
        DosageRequest dosageRequest = new DosageRequest(medicineRequests, reminderRequests);
        PillRegimenRequest pillRegimenRequest = new PillRegimenRequest(externalId, date1, date2, asList(dosageRequest));

        PillRegimen pillRegimen = builder.createFrom(pillRegimenRequest);

        assertEquals(date1, pillRegimen.getStartDate());
        assertEquals(date2, pillRegimen.getEndDate());
        assertEquals(externalId, pillRegimen.getExternalId());
        assertEquals(1, pillRegimen.getDosages().size());
        for (Dosage dosage : pillRegimen.getDosages()) {
            assertEquals(1, dosage.getMedicines().size());
            for (Medicine medicine : dosage.getMedicines()) {
                assertEquals("m1", medicine.getName());
            }
            assertEquals(1, dosage.getReminders().size());
            for (Reminder reminder : dosage.getReminders()) {
                assertEquals(new Integer(1), reminder.getHour());
                assertEquals(new Integer(30), reminder.getMinute());
                assertEquals(new Integer(5), reminder.getRepeatSize());
                assertEquals(new Integer(300), reminder.getRepeatInterval());
            }
        }
    }
}
