package org.motechproject.server.pillreminder.contract;

import java.util.Date;
import java.util.List;

public class DosageRequest {
    private List<String> medicines;
    private List<Date> reminders;

    public DosageRequest(List<String> medicines, List<Date> reminders) {
        this.medicines = medicines;
        this.reminders = reminders;
    }

    public List<String> getMedicines() {
        return medicines;
    }

    public List<Date> getReminders() {
        return reminders;
    }
}
