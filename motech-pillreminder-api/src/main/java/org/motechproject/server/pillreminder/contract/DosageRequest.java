package org.motechproject.server.pillreminder.contract;

import java.util.List;

public class DosageRequest {
    private List<String> medicineRequests;
    private List<ReminderRequest> reminderRequests;

    public DosageRequest(List<String> medicineRequests, List<ReminderRequest> reminderRequests) {
        this.medicineRequests = medicineRequests;
        this.reminderRequests = reminderRequests;
    }

    public List<String> getMedicineRequests() {
        return medicineRequests;
    }

    public List<ReminderRequest> getReminderRequests() {
        return reminderRequests;
    }
}
