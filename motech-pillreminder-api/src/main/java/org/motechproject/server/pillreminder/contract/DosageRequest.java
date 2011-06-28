package org.motechproject.server.pillreminder.contract;

import java.util.List;

public class DosageRequest {
    private int startHour;
    private int startMinute;
    private List<String> medicineRequests;

    public DosageRequest(int startHour, int startMinute, List<String> medicineRequests) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.medicineRequests = medicineRequests;
    }

    public List<String> getMedicineRequests() {
        return medicineRequests;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getStartHour() {
        return startHour;
    }
}
