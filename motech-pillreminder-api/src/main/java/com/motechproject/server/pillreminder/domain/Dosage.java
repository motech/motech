package com.motechproject.server.pillreminder.domain;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dosage dosage = (Dosage) o;
        if (medicines != null ? !medicines.equals(dosage.medicines) : dosage.medicines != null) return false;
        if (reminders != null ? !reminders.equals(dosage.reminders) : dosage.reminders != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = medicines != null ? medicines.hashCode() : 0;
        result = 31 * result + (reminders != null ? reminders.hashCode() : 0);
        return result;
    }
}