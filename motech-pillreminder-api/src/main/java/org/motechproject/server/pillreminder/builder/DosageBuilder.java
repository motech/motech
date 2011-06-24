package org.motechproject.server.pillreminder.builder;

import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.Medicine;
import org.motechproject.server.pillreminder.domain.Reminder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class DosageBuilder {

    public Dosage createFrom(DosageRequest dosageRequest) {
        Set<Medicine> medicines = new HashSet<Medicine>();
        for (String medicineName : dosageRequest.getMedicines()) medicines.add(new Medicine(medicineName));
        Set<Reminder> reminders = new HashSet<Reminder>();
        for (Date date : dosageRequest.getReminders()) reminders.add(new Reminder(date));
        return new Dosage(medicines, reminders);
    }
}
