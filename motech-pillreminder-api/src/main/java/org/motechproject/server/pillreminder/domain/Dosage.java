package org.motechproject.server.pillreminder.domain;

import java.util.Set;

public class Dosage{
    private Set<Medicine> medicines;
    private Set<Reminder> reminders;

    public Dosage() {
    }

    public Dosage(Set<Medicine> medicines, Set<Reminder> reminders) {
        this.medicines = medicines;
        this.reminders = reminders;
    }

    public Set<Medicine> getMedicines() {
        return medicines;
    }

    public void setMedicines(Set<Medicine> medicines) {
        this.medicines = medicines;
    }

    public Set<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(Set<Reminder> reminders) {
        this.reminders = reminders;
    }

}