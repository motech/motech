package org.motechproject.server.pillreminder.builder;

import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.ReminderRequest;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.junit.Test;
import org.motechproject.server.pillreminder.domain.Medicine;
import org.motechproject.server.pillreminder.domain.Reminder;

import java.util.*;

import static java.util.Arrays.asList;
import static org.motechproject.server.pillreminder.util.TestUtil.newDate;
import static org.junit.Assert.assertEquals;

public class DosageBuilderTest {

    private DosageBuilder builder = new DosageBuilder();

    @Test
    public void shouldBuildADosageFromRequest() {
        ReminderRequest reminderRequest = new ReminderRequest(1, 30, 5, 300);
        DosageRequest dosageRequest = new DosageRequest(Arrays.asList("m1"), Arrays.asList(reminderRequest));

        Dosage dosage = builder.createFrom(dosageRequest);

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
